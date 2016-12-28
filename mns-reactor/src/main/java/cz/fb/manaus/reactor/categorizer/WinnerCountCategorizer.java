package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class WinnerCountCategorizer extends AbstractCountCategorizer {

    protected WinnerCountCategorizer() {
        super("winnerCount_", 6);
    }

    @Override
    protected int getCount(SettledBet bet) {
        return bet.getBetAction().getMarketPrices().getWinnerCount();
    }
}
