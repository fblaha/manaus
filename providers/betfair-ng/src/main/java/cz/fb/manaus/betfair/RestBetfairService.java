package cz.fb.manaus.betfair;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.betfair.rest.CancelExecutionReport;
import cz.fb.manaus.betfair.rest.CancelInstruction;
import cz.fb.manaus.betfair.rest.LimitOrder;
import cz.fb.manaus.betfair.rest.OrderType;
import cz.fb.manaus.betfair.rest.PersistenceType;
import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.betfair.rest.PlaceInstruction;
import cz.fb.manaus.betfair.rest.ReplaceExecutionReport;
import cz.fb.manaus.betfair.rest.ReplaceInstruction;
import cz.fb.manaus.betfair.session.RestSession;
import cz.fb.manaus.betfair.session.RestSessionService;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.traffic.ExpensiveOperationModerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class RestBetfairService {
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

    public PlaceExecutionReport placeBets(List<Bet> bets) {
        List<PlaceInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            PlaceInstruction instruction = new PlaceInstruction();
            instruction.setHandicap(0);
            Side side = requireNonNull(bet.getRequestedPrice().getSide());
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
        PlaceExecutionReport report = getRestEntity(betParams(getMarketId(bets), instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/placeOrders/", PlaceExecutionReport.class);
        report.validate();
        moderator.suspendOnExceeded();
        return report;
    }

    private Map<String, ?> betParams(String marketId, List<?> instructions) {
        return ImmutableMap.of("marketId", marketId, "instructions", instructions);
    }

    private String getMarketId(List<Bet> bets) {
        Set<String> ids = bets.stream().map(Bet::getMarketId).collect(Collectors.toSet());
        Preconditions.checkState(ids.size() == 1);
        return ids.stream().findFirst().orElse(null);
    }

    public ReplaceExecutionReport replaceBets(List<Bet> bets) {
        List<ReplaceInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            ReplaceInstruction instruction = new ReplaceInstruction();
            instruction.setBetId(requireNonNull(bet.getBetId()));
            instruction.setNewPrice(bet.getRequestedPrice().getPrice());
            instructions.add(instruction);
        }
        ReplaceExecutionReport report = getRestEntity(betParams(getMarketId(bets), instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/replaceOrders/", ReplaceExecutionReport.class);
        report.validate();
        moderator.suspendOnExceeded();
        return report;
    }

    public CancelExecutionReport cancelBets(List<Bet> bets) {
        List<CancelInstruction> instructions = new LinkedList<>();
        for (Bet bet : bets) {
            CancelInstruction instruction = new CancelInstruction();
            instruction.setBetId(requireNonNull(bet.getBetId()));
            instructions.add(instruction);
        }
        String marketId = getMarketId(bets);
        CancelExecutionReport report = getCancelExecutionReport(marketId, instructions);
        moderator.suspendOnExceeded();
        return report;
    }

    private CancelExecutionReport getCancelExecutionReport(String marketId, List<CancelInstruction> instructions) {
        CancelExecutionReport report = getRestEntity(betParams(marketId, instructions),
                "https://api.betfair.com/exchange/betting/rest/v1.0/cancelOrders/", CancelExecutionReport.class);
        report.validate();
        return report;
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
