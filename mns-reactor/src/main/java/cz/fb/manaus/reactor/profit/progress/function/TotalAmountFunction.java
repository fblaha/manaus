package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class TotalAmountFunction extends AbstractOfferedAmountFunction {

    @Override
    protected RunnerPrices getRunnerPrices(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        return marketPrices.getRunnerPrices(bet.getSelectionId());
    }

}
