package cz.fb.manaus.betfair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.betfair.rest.AccountFunds;
import cz.fb.manaus.betfair.rest.AccountStatementReport;
import cz.fb.manaus.betfair.rest.CancelExecutionReport;
import cz.fb.manaus.betfair.rest.CancelInstruction;
import cz.fb.manaus.betfair.rest.CompetitionResult;
import cz.fb.manaus.betfair.rest.EventResult;
import cz.fb.manaus.betfair.rest.EventTypeResult;
import cz.fb.manaus.betfair.rest.Filter;
import cz.fb.manaus.betfair.rest.IncludeItem;
import cz.fb.manaus.betfair.rest.LimitOrder;
import cz.fb.manaus.betfair.rest.MarketBettingType;
import cz.fb.manaus.betfair.rest.MarketBook;
import cz.fb.manaus.betfair.rest.MarketCatalogue;
import cz.fb.manaus.betfair.rest.MarketProjection;
import cz.fb.manaus.betfair.rest.MatchProjection;
import cz.fb.manaus.betfair.rest.OrderProjection;
import cz.fb.manaus.betfair.rest.OrderType;
import cz.fb.manaus.betfair.rest.Params;
import cz.fb.manaus.betfair.rest.PersistenceType;
import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.betfair.rest.PlaceInstruction;
import cz.fb.manaus.betfair.rest.PriceData;
import cz.fb.manaus.betfair.rest.PriceProjection;
import cz.fb.manaus.betfair.rest.ReplaceExecutionReport;
import cz.fb.manaus.betfair.rest.ReplaceInstruction;
import cz.fb.manaus.betfair.rest.TimeRange;
import cz.fb.manaus.betfair.session.RestSession;
import cz.fb.manaus.betfair.session.RestSessionService;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.traffic.ExpensiveOperationModerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getFirst;
import static cz.fb.manaus.betfair.rest.Params.withFilter;

@Service
public class RestBetfairService {
    public static final ParameterizedTypeReference<List<EventTypeResult>> EVENT_TYPE_LIST = new ParameterizedTypeReference<List<EventTypeResult>>() {
    };
    public static final ParameterizedTypeReference<List<EventResult>> EVENT_LIST = new ParameterizedTypeReference<List<EventResult>>() {
    };
    public static final ParameterizedTypeReference<List<MarketBook>> MARKET_BOOK_LIST = new ParameterizedTypeReference<List<MarketBook>>() {
    };
    public static final ParameterizedTypeReference<List<MarketCatalogue>> MARKET_LIST = new ParameterizedTypeReference<List<MarketCatalogue>>() {
    };
    public static final ParameterizedTypeReference<List<CompetitionResult>> COMPETITION_LIST = new ParameterizedTypeReference<List<CompetitionResult>>() {
    };
    private static final Logger log = Logger.getLogger(RestBetfairService.class.getSimpleName());
    private final ExpensiveOperationModerator moderator = new ExpensiveOperationModerator(Duration.ofMillis(300), "betfair");
    @Autowired
    private RestSessionService sessionService;

    public static <T> T handleHttpClientException(Logger logger, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (HttpClientErrorException e) {
            logger.severe(e.getResponseBodyAsString());
            throw e;
        }
    }

    public List<EventTypeResult> listEventTypes() {
        moderator.suspendOnExceeded();
        Filter filter = createCommonFilter();
        return getRestEntities(withFilter(filter), "https://api.betfair.com/exchange/betting/rest/v1.0/listEventTypes/", EVENT_TYPE_LIST);
    }

    public List<EventResult> listEvents(Set<String> eventTypesIds, Date from, Date to) {
        moderator.suspendOnExceeded();
        Filter filter = createCommonFilter();
        filter.setEventTypeIds(eventTypesIds);
        TimeRange timeRange = new TimeRange();
        timeRange.setFrom(from);
        timeRange.setTo(to);
        filter.setMarketStartTime(timeRange);
        return getRestEntities(withFilter(filter), "https://api.betfair.com/exchange/betting/rest/v1.0/listEvents/", EVENT_LIST);
    }

    public List<CompetitionResult> listCompetitions(String eventTyeId) {
        moderator.suspendOnExceeded();
        Filter filter = createCommonFilter();
        filter.setEventTypeIds(Collections.singleton(eventTyeId));
        return getRestEntities(withFilter(filter), "https://api.betfair.com/exchange/betting/rest/v1.0/listCompetitions/", COMPETITION_LIST);
    }

    public List<MarketCatalogue> listMarkets(Set<String> eventTypesIds, Set<String> eventIds, Date from, Date to) {
        moderator.suspendOnExceeded();
        Filter filter = createCommonFilter();
        filter.setEventTypeIds(eventTypesIds);
        if (!eventIds.isEmpty()) filter.setEventIds(eventIds);
        TimeRange timeRange = new TimeRange();
        timeRange.setFrom(from);
        timeRange.setTo(to);
        filter.setMarketStartTime(timeRange);
        Params params = withFilter(filter);
        params.setMaxResults(200);
        params.setMarketProjection(EnumSet.allOf(MarketProjection.class));
        return getRestEntities(params, "https://api.betfair.com/exchange/betting/rest/v1.0/listMarketCatalogue/", MARKET_LIST);
    }

    private Filter createCommonFilter() {
        Filter filter = new Filter();
        filter.setMarketBettingTypes(Collections.singleton(MarketBettingType.ODDS));
        return filter;
    }

    public List<MarketBook> listMarketBooks(Set<String> marketIds) {
        moderator.suspendOnExceeded();
        Params params = new Params();
        params.setMarketIds(marketIds);
        params.setOrderProjection(OrderProjection.ALL);
        params.setMatchProjection(MatchProjection.ROLLED_UP_BY_PRICE);
        PriceProjection projection = new PriceProjection();
        projection.setPriceData(ImmutableSet.of(PriceData.EX_BEST_OFFERS, PriceData.EX_TRADED, PriceData.EX_ALL_OFFERS));
        params.setPriceProjection(projection);
        return getRestEntities(params, "https://api.betfair.com/exchange/betting/rest/v1.0/listMarketBook/", MARKET_BOOK_LIST);
    }

    public AccountFunds getAccountFunds() {
        moderator.suspendOnExceeded();
        return getRestEntity(new Params(), "https://api.betfair.com/exchange/account/rest/v1.0/getAccountFunds/", AccountFunds.class);
    }

    public AccountStatementReport getAccountStatement(int fromRecord, int recordCount) {
        moderator.suspendOnExceeded();
        Params params = new Params();
        params.setFromRecord(fromRecord);
        params.setRecordCount(recordCount);
        params.setIncludeItem(IncludeItem.EXCHANGE);
        return getRestEntity(params, "https://api.betfair.com/exchange/account/rest/v1.0/getAccountStatement/", AccountStatementReport.class);
    }

    public PlaceExecutionReport placeBets(List<Bet> bets) {
        List<PlaceInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            PlaceInstruction instruction = new PlaceInstruction();
            instruction.setHandicap(0);
            Side side = checkNotNull(bet.getRequestedPrice().getSide());
            if (side == Side.LAY) {
                instruction.setSide(cz.fb.manaus.betfair.rest.Side.LAY);
            } else if (side == Side.BACK) {
                instruction.setSide(cz.fb.manaus.betfair.rest.Side.BACK);
            }
            instruction.setSelectionId(bet.getSelectionId());
            instruction.setOrderType(OrderType.LIMIT);
            LimitOrder limitOrder = new LimitOrder();
            limitOrder.setPrice(bet.getRequestedPrice().getPrice());
            limitOrder.setSize(bet.getRequestedPrice().getAmount());
            limitOrder.setPersistenceType(PersistenceType.LAPSE);
            instruction.setLimitOrder(limitOrder);
            instructions.add(instruction);
        }
        PlaceExecutionReport report = getRestEntity(Params.betParams(getMarketId(bets), instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/placeOrders/", PlaceExecutionReport.class);
        report.validate();
        moderator.suspendOnExceeded();
        return report;
    }

    private String getMarketId(List<Bet> bets) {
        Set<String> ids = bets.stream().map(Bet::getMarketId).collect(Collectors.toSet());
        Preconditions.checkState(ids.size() == 1);
        return getFirst(ids, null);
    }

    public ReplaceExecutionReport replaceBets(List<Bet> bets) {
        List<ReplaceInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            ReplaceInstruction instruction = new ReplaceInstruction();
            instruction.setBetId(checkNotNull(bet.getBetId()));
            instruction.setNewPrice(bet.getRequestedPrice().getPrice());
            instructions.add(instruction);
        }
        ReplaceExecutionReport report = getRestEntity(Params.betParams(getMarketId(bets), instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/replaceOrders/", ReplaceExecutionReport.class);
        report.validate();
        moderator.suspendOnExceeded();
        return report;
    }

    public CancelExecutionReport cancelBets(List<Bet> bets) {
        List<CancelInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            CancelInstruction instruction = new CancelInstruction();
            instruction.setBetId(checkNotNull(bet.getBetId()));
            instructions.add(instruction);
        }
        String marketId = getMarketId(bets);
        CancelExecutionReport report = getCancelExecutionReport(marketId, instructions);
        moderator.suspendOnExceeded();
        return report;
    }

    private CancelExecutionReport getCancelExecutionReport(String marketId, List<CancelInstruction> instructions) {
        CancelExecutionReport report = getRestEntity(Params.betParams(marketId, instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/cancelOrders/", CancelExecutionReport.class);
        report.validate();
        return report;
    }


    private <T> List<T> getRestEntities(Params params, String url, ParameterizedTypeReference<List<T>> responseType) {
        RestSession cachedSession = sessionService.getCachedSession();
        HttpHeaders httpHeaders = sessionService.createCommonHeaders(cachedSession.getToken(), true);
        HttpEntity<Params> httpEntity = new HttpEntity<>(params, httpHeaders);
        try {
            ResponseEntity<List<T>> responseEntity = handleHttpClientException(log,
                    () -> cachedSession.getTemplate().exchange(url, HttpMethod.POST, httpEntity, responseType));
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            log.severe(e.getResponseBodyAsString());
            throw e;
        }
    }

    private <T, P> T getRestEntity(P params, String url, Class<T> clazz) {
        RestSession cachedSession = sessionService.getCachedSession();
        HttpHeaders httpHeaders = sessionService.createCommonHeaders(cachedSession.getToken(), true);
        HttpEntity<P> httpEntity = new HttpEntity<>(params, httpHeaders);
        ResponseEntity<T> responseEntity = handleHttpClientException(log,
                () -> cachedSession.getTemplate().exchange(url, HttpMethod.POST, httpEntity, clazz));
        return responseEntity.getBody();
    }

}
