package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class MySideAmountFunction extends AbstractOfferedAmountFunction {

    @Override
    protected RunnerPrices getRunnerPrices(SettledBet bet) {
        var marketPrices = bet.getBetAction().getMarketPrices();
        var prices = marketPrices.getRunnerPrices(bet.getSelectionId());
        var side = bet.getPrice().getSide();
        return prices.getHomogeneous(side);
    }

}
