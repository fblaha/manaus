package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;

import java.util.List;
import java.util.Optional;

public interface BetService {

    Optional<String> validate(BetEndpoint endpoint);

    List<String> placeBets(BetEndpoint endpoint, List<Bet> bets);

    List<String> updateBets(BetEndpoint endpoint, List<Bet> bets);

    void cancelBets(BetEndpoint endpoint, List<Bet> bets);
}
