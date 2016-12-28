package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Bet;

import java.util.function.Consumer;

public class BetCommand {

    private final Bet newBet;
    private final Consumer<String> handler;

    public BetCommand(Bet newBet, Consumer<String> handler) {
        this.newBet = newBet;
        this.handler = handler;
    }

    public Bet getNewBet() {
        return newBet;
    }

    public Consumer<String> getBetIdHandler() {
        return handler;
    }
}


