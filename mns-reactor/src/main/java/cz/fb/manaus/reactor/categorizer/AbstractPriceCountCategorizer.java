package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;

public abstract class AbstractPriceCountCategorizer extends AbstractCountCategorizer {

    private final Side side;

    public AbstractPriceCountCategorizer(String prefix, int maxCount, Side side) {
        super(prefix, maxCount);
        this.side = side;
    }

    @Override
    protected int getCount(SettledBet bet) {
        return bet.getBetAction().getMarketPrices().getRunnerPrices(bet.getSelectionId())
                .getHomogeneous(side).getPrices().size();
    }
}
