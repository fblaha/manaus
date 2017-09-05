package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;

public class BetCommand {

    private final Bet bet;
    private final BetAction action;

    public BetCommand(Bet bet, BetAction action) {
        this.bet = bet;
        this.action = action;
    }

    public Bet getBet() {
        return bet;
    }

    public BetAction getAction() {
        return action;
    }
}


