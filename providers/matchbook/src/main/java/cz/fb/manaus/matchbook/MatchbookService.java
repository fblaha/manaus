package cz.fb.manaus.matchbook;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
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
import cz.fb.manaus.matchbook.rest.SettledBets;
import cz.fb.manaus.matchbook.rest.Settlement;
import cz.fb.manaus.matchbook.rest.SettlementPage;
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
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.FluentIterable.from;

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

    private String rest(String path) {
        return MatchbookSessionService.REST + path;
    }

    private String oldRest(String path) {
        return MatchbookSessionService.OLD_REST + path;
    }

    public void walkMarkets(Instant from, Instant to, Consumer<MarketSnapshot> consumer) {
        for (String sport : sports) {
            pagedWalk(offset -> getEvents(from, to, sport, offset),
                    eventPage -> processEventPage(eventPage, consumer));
        }
    }

    private void processEventPage(EventPage eventPage, Consumer<MarketSnapshot> consumer) {
        ListMultimap<String, Bet> bets = LinkedListMultimap.create();
        List<Event> events = from(eventPage.getEvents())
                .filter(e -> e.getMetaTags() != null)
                .filter(Predicates.not(Event::isInRunningFlag))
                .toList();
        if (!events.isEmpty()) {
            ImmutableList<Long> eventIds = from(events).transform(Event::getId).toList();
            List<Offer> offers = getOffers(eventIds);
            List<Bet> rawBets = from(offers).transform(modelConverter::toModel).toList();
            List<Bet> genuineBets = betUtils.filterDuplicates(rawBets);
            for (Bet bet : genuineBets) {
                bets.put(bet.getMarketId(), bet);
            }

            for (Event event : events) {
                FluentIterable<Market> markets = from(event.getMarkets())
                        .filter(Predicates.not(Market::isInRunningFlag));
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
            urlPath += "&" + Preconditions.checkNotNull(URI_PARAMS.get(sport));
        }
        ResponseEntity<EventPage> responseEntity = sessionService.getTemplate().exchange(
                rest(urlPath),
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
                .exchange(rest("offers?event-ids={eventIds}&offset={offset}&per-page={perPage}&status={status}"),
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
        String offersUrl = rest("offers");

        ResponseEntity<PlaceReport> responseEntity = sessionService.getTemplate().exchange(offersUrl, method,
                new HttpEntity<>(offers), PlaceReport.class);
        String result[] = new String[bets.size()];
        PlaceReport report = checkResponse(responseEntity).getBody();

        AccountMoney accountMoney = new AccountMoney(report.getBalance(), report.getAvailableAmount());
        accountMoneyRegistry.register(accountMoney);
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
            template.delete(rest("offers/{offerId}"), bet.getBetId());
        }
    }

    @Override
    public AccountMoney getAccountMoney() {
        moneyModerator.suspendOnExceeded();
        ResponseEntity<Balance> entity = sessionService.getTemplate().exchange(rest("account/balance"), HttpMethod.GET, null, Balance.class);
        checkResponse(entity);
        Balance balance = entity.getBody();
        return new AccountMoney(balance.getBalance(), balance.getFreeFunds());
    }

    public List<SettledBet> getSettledBets(long marketId, long selectionId) {
        ResponseEntity<SettledBets> responseEntity = sessionService.getTemplate()
                .exchange(rest("reports/settlements/{marketId}/runners/{runnerId}"),

                        HttpMethod.GET, null, SettledBets.class, marketId, selectionId);
        List<cz.fb.manaus.matchbook.rest.SettledBet> bets = checkResponse(responseEntity).getBody().getBets();
        return from(bets).transform(modelConverter::toModel)
                // TODO filter by selection does not work
                .filter(bet -> bet.getSelectionId() == selectionId)
                .toList();
    }

    public void walkSettlements(Instant from, Consumer<Settlement> consumer) {
        pagedWalk(offset -> getSettlementPage(from, offset),
                settlementPage -> settlementPage.getMarkets().forEach(consumer));
    }

    private SettlementPage getSettlementPage(Instant from, int offset) {
        settledBetsModerator.suspendOnExceeded();
        ResponseEntity<SettlementPage> responseEntity = sessionService.getTemplate()
                .exchange(oldRest("reports/settlements?offset={offset}&after={after}"),
                        HttpMethod.GET, null, SettlementPage.class, offset, from.getEpochSecond());
        return checkResponse(responseEntity).getBody();
    }

}
