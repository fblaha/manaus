package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;

import java.util.List;

public interface BetService {

    List<String> placeBets(BetEndpoint endpoint, List<Bet> bets);

    List<String> updateBets(BetEndpoint endpoint, List<Bet> bets);

    void cancelBets(BetEndpoint endpoint, List<Bet> bets);
}
