package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.OptionalDouble;
import java.util.Set;

@Component
public class LastMatchedReciprocalCategorizer implements SettledBetCategorizer {


    public static final String PREFIX = "lastMatchedReciprocal_";

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        MarketPrices marketPrices = settledBet.getBetAction().getMarketPrices();
        OptionalDouble reciprocal = marketPrices.getLastMatchedReciprocal();
        if (reciprocal.isPresent()) {
            if (Price.priceEq(reciprocal.getAsDouble(), 1d)) {
                return Collections.singleton(PREFIX + "eq1");
            } else if (reciprocal.getAsDouble() > 1d) {
                return Collections.singleton(PREFIX + "above1");
            } else {
                return Collections.singleton(PREFIX + "bellow1");
            }
        }
        return Set.of();
    }
}
