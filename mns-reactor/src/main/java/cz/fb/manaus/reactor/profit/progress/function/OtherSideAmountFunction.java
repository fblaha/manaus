package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

@Component
public class OtherSideAmountFunction extends AbstractOfferedAmountFunction {

    @Override
    protected RunnerPrices getRunnerPrices(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        RunnerPrices prices = marketPrices.getRunnerPrices(bet.getSelectionId());
        Side side = bet.getPrice().getSide().getOpposite();
        return prices.getHomogeneous(side);
    }

}
