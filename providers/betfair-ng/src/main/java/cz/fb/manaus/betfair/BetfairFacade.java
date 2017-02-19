package cz.fb.manaus.betfair;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.betfair.rest.AccountFunds;
import cz.fb.manaus.betfair.rest.AccountStatement;
import cz.fb.manaus.betfair.rest.AccountStatementReport;
import cz.fb.manaus.betfair.rest.EventResult;
import cz.fb.manaus.betfair.rest.EventTypeResult;
import cz.fb.manaus.betfair.rest.ExchangePrices;
import cz.fb.manaus.betfair.rest.MarketBook;
import cz.fb.manaus.betfair.rest.MarketCatalogue;
import cz.fb.manaus.betfair.rest.MarketCountAware;
import cz.fb.manaus.betfair.rest.MarketDescription;
import cz.fb.manaus.betfair.rest.MarketStatus;
import cz.fb.manaus.betfair.rest.Order;
import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.betfair.rest.PlaceInstructionReport;
import cz.fb.manaus.betfair.rest.PriceSize;
import cz.fb.manaus.betfair.rest.ReplaceExecutionReport;
import cz.fb.manaus.betfair.rest.ReplaceInstructionReport;
import cz.fb.manaus.betfair.rest.RunnerCatalog;
import cz.fb.manaus.betfair.rest.StatementLegacyData;
import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Competition;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.reactor.betting.BetService;
import cz.fb.manaus.reactor.traffic.BetTransactionLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static cz.fb.manaus.betfair.rest.MarketCountAware.split;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.math3.util.Precision.round;

@Service
public class BetfairFacade implements BetService {

    private static final Logger log = Logger.getLogger(BetfairFacade.class.getSimpleName());

    private final Set<String> eventTypes;
    @Autowired
    private RestBetfairService service;
    @Autowired
    private BetTransactionLogger transactionLogger;

    @Autowired
    public BetfairFacade(@Value("#{systemEnvironment['MNS_BETFAIR_EVENT_TYPES_NAMES']}") String rawEventTypes) {
        this.eventTypes = ImmutableSet.copyOf(Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .split(Strings.nullToEmpty(rawEventTypes).toLowerCase()));
    }

    @Override
    public AccountMoney getAccountMoney() {
        AccountFunds funds = service.getAccountFunds();
        double total = round(funds.getAvailableToBetBalance() + abs(funds.getExposure()), 3);
        return new AccountMoney(total, funds.getAvailableToBetBalance());
    }

    public void walkMarkets(Date from, Date to, Consumer<Market> consumer) {
        FluentIterable<EventTypeResult> typeResults = from(service.listEventTypes());
        log.log(Level.INFO, "Allowed event types ''{0}''", eventTypes);
        typeResults = typeResults.filter(type -> {
            String name = type.getEventType().getName().toLowerCase();
            boolean result = eventTypes.contains(name);
            if (!result) {
                log.log(Level.INFO, "Excluded event types ''{0}''", name);
            }
            return result;
        });

        for (List<EventTypeResult> results : split(typeResults.filter(not(MarketCountAware::isOverLimit)))) {
            results.forEach(result -> log.log(Level.INFO, "Processing small event type ''{0}''", result));
            Set<String> typeIds = results.stream()
                    .map(EventTypeResult::getEventType)
                    .map(cz.fb.manaus.betfair.rest.EventType::getId)
                    .collect(toSet());
            List<MarketCatalogue> marketCatalogues = service.listMarkets(typeIds, Collections.emptySet(), from, to);
            marketCatalogues.stream().map(this::toMarket).forEach(consumer);
        }
        for (EventTypeResult result : typeResults.filter(MarketCountAware::isOverLimit)) {
            log.log(Level.INFO, "Processing big event type ''{0}''", result);
            Set<String> eventTypesIds = Collections.singleton(result.getEventType().getId());
            List<EventResult> allEvents = service.listEvents(eventTypesIds, from, to);
            allEvents.stream().filter(EventResult::isOverLimit)
                    .forEach(e -> log.log(Level.SEVERE, "Too big event ''{0}''", e));
            allEvents.forEach(MarketCountAware::isOverLimit);
            for (List<EventResult> events : split(allEvents)) {
                events.forEach(e -> log.log(Level.INFO, "Processing event ''{0}''", e));
                Set<String> eventIds = events.stream()
                        .map(EventResult::getEvent)
                        .map(cz.fb.manaus.betfair.rest.EventType::getId)
                        .collect(toSet());
                List<MarketCatalogue> catalogues = getMarketCatalogues(from, to, eventTypesIds, eventIds);
                catalogues.stream().map(this::toMarket).forEach(consumer);
            }
        }
    }

    private List<MarketCatalogue> getMarketCatalogues(Date from, Date to, Set<String> eventTypesIds, Set<String> eventIds) {
        try {
            return service.listMarkets(eventTypesIds, eventIds, from, to);
        } catch (HttpServerErrorException e) {
            log.log(Level.SEVERE, "Betfair server error", e);
            return Collections.emptyList();
        }
    }

    private Market toMarket(MarketCatalogue catalogue) {
        MarketDescription description = catalogue.getDescription();
        Market market = new Market();

        market.setId(catalogue.getMarketId());
        market.setName(catalogue.getMarketName());
        market.setMatchedAmount(catalogue.getTotalMatched());
        market.setBspMarket(description.getBspMarket());
        market.setInPlay(description.getTurnInPlayEnabled());

        market.setType(description.getMarketType());
        cz.fb.manaus.betfair.rest.Competition competition = catalogue.getCompetition();
        if (competition != null) {
            market.setCompetition(new Competition(competition.getId(), competition.getName()));
        }
        cz.fb.manaus.betfair.rest.EventType eventType = catalogue.getEventType();
        market.setEventType(new EventType(eventType.getId(), eventType.getName()));
        Event modelEvent = new Event();
        cz.fb.manaus.betfair.rest.Event event = catalogue.getEvent();
        modelEvent.setCountryCode(event.getCountryCode());
        modelEvent.setCountryCode(event.getCountryCode());
        modelEvent.setId(event.getId());
        modelEvent.setName(event.getName());
        modelEvent.setTimezone(event.getTimezone());
        modelEvent.setOpenDate(event.getOpenDate());
        modelEvent.setVenue(event.getVenue());
        market.setEvent(modelEvent);
        List<Runner> runners = new LinkedList<>();
        market.setRunners(runners);
        for (RunnerCatalog runnerCatalog : catalogue.getRunners()) {
            runners.add(new Runner(runnerCatalog.getSelectionId(), runnerCatalog.getRunnerName(),
                    runnerCatalog.getHandicap(), runnerCatalog.getSortPriority()));
        }
        return market;
    }

    @Override
    public List<String> placeBets(List<Bet> bets) {
        transactionLogger.incrementBy(bets.size(), true);
        PlaceExecutionReport report = service.placeBets(bets);
        return from(report.getInstructionReports()).transform(PlaceInstructionReport::getBetId).toList();
    }

    @Override
    public List<String> updateBets(List<Bet> newBets) {
        transactionLogger.incrementBy(newBets.size(), false);
        ReplaceExecutionReport report = service.replaceBets(newBets);
        return from(report.getInstructionReports())
                .transform(ReplaceInstructionReport::getPlaceInstructionReport)
                .transform(PlaceInstructionReport::getBetId).toList();
    }

    @Override
    public void cancelBets(List<Bet> bets) {
        service.cancelBets(bets);
    }

    public Map<String, SettledBet> getSettledBets(int head, int count) {
        AccountStatementReport accountStatement = service.getAccountStatement(head, count);
        Iterable<AccountStatement> statements = from(accountStatement.getAccountStatement())
                .filter(s -> s.getLegacyData().getWinLose().isSignificant());
        Map<String, AccountStatement> byRefId = uniqueIndex(statements, AccountStatement::getRefId);
        Map<String, SettledBet> result = transformValues(byRefId, this::toSettledBet);
        return ImmutableMap.copyOf(result);
    }

    private SettledBet toSettledBet(AccountStatement statement) {
        StatementLegacyData legacyData = statement.getLegacyData();
        long selectionId = legacyData.getSelectionId();
        Side side;
        if ("B".equals(legacyData.getBetType())) {
            side = Side.BACK;
        } else if ("L".equals(legacyData.getBetType())) {
            side = Side.LAY;
        } else {
            throw new IllegalStateException();
        }
        SettledBet result = new SettledBet(selectionId, legacyData.getSelectionName(),
                statement.getAmount(), statement.getItemDate(),
                new Price(legacyData.getAvgPrice(), legacyData.getBetSize(), side));
        result.setPlaced(legacyData.getPlacedDate());
        return result;
    }

    public Map<String, MarketSnapshot> getSnapshot(Set<String> marketIds) {
        List<MarketBook> marketBooks = from(service.listMarketBooks(marketIds))
                .filter(book -> book.getStatus() == MarketStatus.OPEN).toList();
        ImmutableMap<String, MarketBook> byId = uniqueIndex(marketBooks, MarketBook::getMarketId);
        return transformValues(byId, this::toSnapshot);
    }

    private MarketSnapshot toSnapshot(MarketBook book) {
        List<cz.fb.manaus.betfair.rest.Runner> runners = book.getRunners();
        ImmutableList<RunnerPrices> runnerPrices = from(runners).transform(this::toRunnerPrices).toList();
        List<Bet> currentBets = new LinkedList<>();
        for (cz.fb.manaus.betfair.rest.Runner runner : runners) {
            List<Order> orders = runner.getOrders();
            if (orders != null) {
                for (Order order : orders) {
                    Price price = new Price(order.getPrice(), order.getSize(), Side.valueOf(order.getSide()));
                    currentBets.add(new Bet(order.getBetId(), book.getMarketId(), runner.getSelectionId(), price,
                            order.getPlacedDate(), order.getSizeMatched()));
                }
            }
        }
        MarketPrices marketPrices = new MarketPrices(book.getNumberOfWinners(), null, runnerPrices);

        return new MarketSnapshot(marketPrices, currentBets, Optional.of(getTradedVolume(runners)));
    }

    private Map<Long, TradedVolume> getTradedVolume(List<cz.fb.manaus.betfair.rest.Runner> runners) {
        Map<Long, TradedVolume> tradedVolume = new HashMap<>();
        for (cz.fb.manaus.betfair.rest.Runner runner : runners) {
            List<Price> prices = new LinkedList<>();
            for (PriceSize priceSize : runner.getEx().getTradedVolume()) {
                if (!Price.amountEq(priceSize.getSize(), 0d)) {
                    prices.add(new Price(priceSize.getPrice(), priceSize.getSize(), null));
                }
            }
            tradedVolume.put(runner.getSelectionId(), new TradedVolume(prices));
        }
        return tradedVolume;
    }

    private RunnerPrices toRunnerPrices(cz.fb.manaus.betfair.rest.Runner runner) {
        ExchangePrices exchangePrices = runner.getEx();
        FluentIterable<Price> prices = from(exchangePrices.getAvailableToLay())
                .transform(size -> new Price(size.getPrice(), size.getSize(), Side.LAY))
                .append(from(exchangePrices.getAvailableToBack())
                        .transform(size -> new Price(size.getPrice(), size.getSize(), Side.BACK)));
        Double totalMatched = runner.getTotalMatched();
        if (totalMatched == null) {
            totalMatched = 0d;
        }

        return new RunnerPrices(runner.getSelectionId(), prices.toList(), totalMatched, runner.getLastPriceTraded());
    }

}
