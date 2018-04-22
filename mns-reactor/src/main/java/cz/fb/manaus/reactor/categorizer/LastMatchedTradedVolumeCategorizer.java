package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.OptionalDouble;
import java.util.Set;

@Component
public class LastMatchedTradedVolumeCategorizer implements SettledBetCategorizer {

    public static final String LAST_MATCHED = "lastMatched";


    private String getCategory(double tradedMean, double lastMatched) {
        if (Price.priceEq(tradedMean, lastMatched)) {
            return LAST_MATCHED + "_eqTradedMean";
        } else if (lastMatched > tradedMean) {
            return LAST_MATCHED + "_gtTradedMean";
        } else {
            return LAST_MATCHED + "_ltTradedMean";
        }
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        BetAction action = settledBet.getBetAction();
        OptionalDouble tradedMean = action.getDoubleProperty(BetAction.TRADED_VOL_MEAN);
        if (tradedMean.isPresent()) {
            MarketPrices marketPrices = settledBet.getBetAction().getMarketPrices();
            double lastMatchedPrice = marketPrices.getRunnerPrices(settledBet.getSelectionId()).getLastMatchedPrice();
            return Collections.singleton(getCategory(tradedMean.getAsDouble(), lastMatchedPrice));
        } else {
            return Set.of();
        }
    }
}
