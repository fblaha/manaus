package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;

public class BetCommand {

    private final Bet newBet;
    private final BetAction action;

    public BetCommand(Bet newBet, BetAction action) {
        this.newBet = newBet;
        this.action = action;
    }

    public Bet getNewBet() {
        return newBet;
    }

    public BetAction getAction() {
        return action;
    }
}


