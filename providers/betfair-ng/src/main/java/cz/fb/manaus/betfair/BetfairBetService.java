package cz.fb.manaus.betfair;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.reactor.betting.BetEndpoint;
import cz.fb.manaus.reactor.betting.BetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BetfairBetService implements BetService {

    @Override
    public Optional<String> validate(BetEndpoint endpoint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> placeBets(BetEndpoint endpoint, List<Bet> bets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> updateBets(BetEndpoint endpoint, List<Bet> newBets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelBets(BetEndpoint endpoint, List<Bet> bets) {
        throw new UnsupportedOperationException();
    }
}
