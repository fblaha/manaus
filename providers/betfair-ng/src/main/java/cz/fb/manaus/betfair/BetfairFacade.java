package cz.fb.manaus.betfair;

import cz.fb.manaus.betfair.rest.PlaceExecutionReport;
import cz.fb.manaus.betfair.rest.PlaceInstructionReport;
import cz.fb.manaus.betfair.rest.ReplaceExecutionReport;
import cz.fb.manaus.betfair.rest.ReplaceInstructionReport;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.reactor.betting.BetEndpoint;
import cz.fb.manaus.reactor.betting.BetService;
import cz.fb.manaus.reactor.traffic.BetTransactionLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BetfairFacade implements BetService {

    private static final Logger log = Logger.getLogger(BetfairFacade.class.getSimpleName());

    @Autowired
    private RestBetfairService service;
    @Autowired
    private BetTransactionLogger transactionLogger;

    @Override
    public List<String> placeBets(BetEndpoint endpoint, List<Bet> bets) {
        transactionLogger.incrementBy(bets.size(), true);
        PlaceExecutionReport report = service.placeBets(bets);
        return report.getInstructionReports().stream()
                .map(PlaceInstructionReport::getBetId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> updateBets(BetEndpoint endpoint, List<Bet> newBets) {
        transactionLogger.incrementBy(newBets.size(), false);
        ReplaceExecutionReport report = service.replaceBets(newBets);
        return report.getInstructionReports().stream()
                .map(ReplaceInstructionReport::getPlaceInstructionReport)
                .map(PlaceInstructionReport::getBetId)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBets(BetEndpoint endpoint, List<Bet> bets) {
        service.cancelBets(bets);
    }

}
