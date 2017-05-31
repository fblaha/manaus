package cz.fb.manaus.matchbook;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.money.AccountMoneyRegistry;
import cz.fb.manaus.matchbook.rest.AbstractPage;
import cz.fb.manaus.matchbook.rest.Balance;
import cz.fb.manaus.matchbook.rest.Event;
import cz.fb.manaus.matchbook.rest.EventPage;
import cz.fb.manaus.matchbook.rest.Market;
import cz.fb.manaus.matchbook.rest.Offer;
import cz.fb.manaus.matchbook.rest.OfferPage;
import cz.fb.manaus.matchbook.rest.PlaceOffers;
import cz.fb.manaus.matchbook.rest.PlaceReport;
import cz.fb.manaus.matchbook.rest.SettledPage;
import cz.fb.manaus.reactor.betting.BetService;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.reactor.traffic.ExpensiveOperationModerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNull;

@Service
public class MatchbookService implements BetService {

    private static final Map<String, String> URI_PARAMS = ImmutableMap.of(
            "soccer", "market-types=multirunner&grading-types=single-winner-wins");
    private final ExpensiveOperationModerator eventsModerator = new ExpensiveOperationModerator(Duration.ofSeconds(10), "events");
    private final ExpensiveOperationModerator offerModerator = new ExpensiveOperationModerator(Duration.ofSeconds(10), "offers");
    private final ExpensiveOperationModerator moneyModerator = new ExpensiveOperationModerator(Duration.ofSeconds(20), "money");
    private final ExpensiveOperationModerator settledBetsModerator = new ExpensiveOperationModerator(Duration.ofSeconds(10), "settledBets");
    @Autowired
    private MatchbookSessionService sessionService;
    @Autowired
    private ModelConverter modelConverter;
    @Autowired
    private AccountMoneyRegistry accountMoneyRegistry;
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private MatchbookEndpointManager endpointManager;
    private Set<String> sports;

    @Autowired
    public MatchbookService(@Value("#{systemEnvironment['MNS_MATCHBOOK_SPORTS'] ?: 'soccer'}") String rawSports) {
        this.sports = ImmutableSet.copyOf(Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .split(nullToEmpty(rawSports).toLowerCase()));
    }

    static <T> ResponseEntity<T> checkResponse(ResponseEntity<T> responseEntity) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        checkState(statusCode.is2xxSuccessful(), statusCode.getReasonPhrase());
        return responseEntity;
    }


    public void walkMarkets(Instant from, Instant to, Consumer<MarketSnapshot> consumer) {
        for (String sport : sports) {
            pagedWalk(offset -> getEvents(from, to, sport, offset),
                    eventPage -> processEventPage(eventPage, consumer));
        }
    }

    private void processEventPage(EventPage eventPage, Consumer<MarketSnapshot> consumer) {
        ListMultimap<String, Bet> bets = LinkedListMultimap.create();
        Predicate<Event> runningFlag = Event::isInRunningFlag;
        List<Event> events = eventPage.getEvents().stream()
                .filter(e -> e.getMetaTags() != null)
                .filter(runningFlag.negate())
                .collect(Collectors.toList());
        if (!events.isEmpty()) {
            List<Long> eventIds = events.stream().map(Event::getId)
                    .collect(Collectors.toList());
            List<Offer> offers = getOffers(eventIds);
            List<Bet> rawBets = offers.stream().map(modelConverter::toModel)
                    .collect(Collectors.toList());
            List<Bet> genuineBets = betUtils.filterDuplicates(rawBets);
            for (Bet bet : genuineBets) {
                bets.put(bet.getMarketId(), bet);
            }

            for (Event event : events) {
                Predicate<Market> marketRunningFlag = Market::isInRunningFlag;
                List<Market> markets = event.getMarkets().stream()
                        .filter(marketRunningFlag.negate())
                        .collect(Collectors.toList());
                for (Market market : markets) {
                    MarketPrices marketPrices = modelConverter.toModel(event, market);
                    MarketSnapshot marketSnapshot = new MarketSnapshot(marketPrices,
                            bets.get(Long.toString(market.getId())), Optional.empty());
                    consumer.accept(marketSnapshot);
                }
            }
        }
    }

    private EventPage getEvents(Instant from, Instant to, String sport, int offset) {
        eventsModerator.suspendOnExceeded();
        String urlPath = "events?include-markets=true&include-runners=true&include-prices=true" +
                "&offset={offset}&per-page={perPage}&after={after}&before={before}" +
                "&tag-url-names={sport}";
        if (URI_PARAMS.containsKey(sport)) {
            urlPath += "&" + requireNonNull(URI_PARAMS.get(sport));
        }
        ResponseEntity<EventPage> responseEntity = sessionService.getTemplate().exchange(
                endpointManager.rest(urlPath),
                HttpMethod.GET, null, EventPage.class, offset, 20,
                from.getEpochSecond(), to.getEpochSecond(), sport);
        return checkResponse(responseEntity).getBody();
    }

    private List<Offer> getOffers(List<Long> eventIds) {
        offerModerator.suspendOnExceeded();
        List<Offer> offers = new LinkedList<>();
        pagedWalk(offset -> getOfferPage(eventIds, "unmatched", offset), page -> offers.addAll(page.getOffers()));
        pagedWalk(offset -> getOfferPage(eventIds, "matched", offset), page -> offers.addAll(page.getOffers()));
        return offers;
    }

    private OfferPage getOfferPage(List<Long> eventIds, String status, int offset) {
        String events = Joiner.on(',').join(eventIds);
        ResponseEntity<OfferPage> responseEntity = sessionService.getTemplate()
                .exchange(endpointManager.rest("offers?event-ids={eventIds}&offset={offset}&per-page={perPage}&status={status}"),
                        HttpMethod.GET, null, OfferPage.class, events, offset, 50, status);
        return checkResponse(responseEntity).getBody();
    }

    private <T extends AbstractPage> void pagedWalk(Function<Integer, T> offsetFunc, Consumer<T> consumer) {
        int offset = 0;
        T page;
        do {
            page = offsetFunc.apply(offset);
            consumer.accept(page);
            offset += page.getPerPage();
        } while (offset < page.getTotal());
    }

    @Override
    public List<String> placeBets(List<Bet> bets) {
        return placeOrUpdate(bets);
    }

    private List<String> placeOrUpdate(List<Bet> bets) {
        PlaceOffers offers = new PlaceOffers();
        offers.setExchangeType("back-lay");
        offers.setOddsType("DECIMAL");
        LinkedList<Offer> items = new LinkedList<>();
        boolean update = bets.stream().allMatch(b -> b.getBetId() != null);
        Map<Long, Integer> indices = new HashMap<>();
        for (int i = 0; i < bets.size(); i++) {
            Bet bet = bets.get(i);
            Offer offer = new Offer();
            if (bet.getBetId() != null) {
                long id = Long.parseLong(bet.getBetId());
                offer.setId(id);
                indices.put(id, i);
            }
            Price requestedPrice = bet.getRequestedPrice();
            offer.setSide(requestedPrice.getSide().name().toLowerCase());
            offer.setOdds(requestedPrice.getPrice());
            offer.setStake(requestedPrice.getAmount());
            offer.setTempId(i);
            offer.setRunnerId(bet.getSelectionId());
            items.add(offer);
        }
        HttpMethod method;
        if (update) {
            method = HttpMethod.PUT;
            checkState(items.stream().noneMatch(offer -> offer.getId() == null));
        } else {
            method = HttpMethod.POST;
            checkState(items.stream().allMatch(offer -> offer.getId() == null));
        }
        offers.setOffers(items);
        String offersUrl = endpointManager.rest("offers");

        ResponseEntity<PlaceReport> responseEntity = sessionService.getTemplate().exchange(offersUrl, method,
                new HttpEntity<>(offers), PlaceReport.class);
        String result[] = new String[bets.size()];
        PlaceReport report = checkResponse(responseEntity).getBody();
        // TODO fix it
        // AccountMoney accountMoney = new AccountMoney(report.getBalance(), report.getAvailableAmount());
        // accountMoneyRegistry.register(accountMoney);
        for (Offer offer : report.getOffers()) {
            int index = update ? indices.get(offer.getId()) : (int) offer.getTempId();
            result[index] = Long.toString(offer.getId());
        }
        return ImmutableList.copyOf(result);
    }

    @Override
    public List<String> updateBets(List<Bet> bets) {
        return placeBets(bets);
    }

    @Override
    public void cancelBets(List<Bet> bets) {
        RestTemplate template = sessionService.getTemplate();
        for (Bet bet : bets) {
            template.delete(endpointManager.rest("offers/{offerId}"), bet.getBetId());
        }
    }

    @Override
    public AccountMoney getAccountMoney() {
        moneyModerator.suspendOnExceeded();
        ResponseEntity<Balance> entity = sessionService.getTemplate().exchange(
                endpointManager.rest("account/balance"), HttpMethod.GET, null, Balance.class);
        checkResponse(entity);
        Balance balance = entity.getBody();
        return new AccountMoney(balance.getBalance(), balance.getFreeFunds());
    }

    public void walkSettledBets(Instant from, BiConsumer<String, SettledBet> consumer) {
        pagedWalk(offset -> getSettlementPage(from, offset),
                settledPage -> walkSettledPage(settledPage, consumer));
    }

    private void walkSettledPage(SettledPage page, BiConsumer<String, SettledBet> consumer) {
        for (Map.Entry<String, SettledBet> bet : modelConverter.toModel(page).entrySet()) {
            consumer.accept(bet.getKey(), bet.getValue());
        }
    }

    private SettledPage getSettlementPage(Instant from, int offset) {
        settledBetsModerator.suspendOnExceeded();
        ResponseEntity<SettledPage> responseEntity = sessionService.getTemplate()
                .exchange(endpointManager.rest("reports/v1/bets/settled?offset={offset}&after={after}"),
                        HttpMethod.GET, null, SettledPage.class, offset, from);
        return checkResponse(responseEntity).getBody();
    }

}
