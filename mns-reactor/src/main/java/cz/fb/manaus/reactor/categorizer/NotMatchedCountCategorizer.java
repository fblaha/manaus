package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class NotMatchedCountCategorizer extends AbstractCountCategorizer {


    protected NotMatchedCountCategorizer() {
        super("notMatchedCount_", 4);
    }

    @Override
    protected int getCount(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        return (int) marketPrices.getRunnerPrices().stream()
                .filter(rp -> rp.getLastMatchedPrice() == null).count();
    }
}
