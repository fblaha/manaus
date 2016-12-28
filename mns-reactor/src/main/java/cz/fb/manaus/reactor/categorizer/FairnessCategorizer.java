package cz.fb.manaus.reactor.categorizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static com.google.common.collect.Range.closedOpen;

@Component
public class FairnessCategorizer implements SettledBetCategorizer {

    public static final RangeMap<Double, String> RANGES = ImmutableRangeMap.<Double, String>builder()
            .put(Range.downTo(1d, BoundType.CLOSED), "1.00+")
            .put(closedOpen(0.95d, 1.00d), "0.95-1.00")
            .put(closedOpen(0.90d, 0.95d), "0.90-0.95")
            .put(closedOpen(0.85d, 0.90d), "0.85-0.90")
            .put(closedOpen(0.80d, 0.85d), "0.80-0.85")
            .put(closedOpen(0.75d, 0.80d), "0.75-0.80")
            .put(closedOpen(0.70d, 0.75d), "0.70-0.75")

            .put(closedOpen(0.6d, 0.7d), "0.60-0.70")
            .put(closedOpen(0.5d, 0.6d), "0.50-0.60")
            .put(closedOpen(0.4d, 0.5d), "0.40-0.50")
            .put(closedOpen(0.3d, 0.4d), "0.30-0.40")
            .put(closedOpen(0.2d, 0.3d), "0.20-0.30")
            .put(closedOpen(0.1d, 0.2d), "0.10-0.20")
            .put(closedOpen(0.0d, 0.1d), "0.00-0.10").build();

    public static final String PREFIX = "fairness_";

    @Autowired
    private FairnessPolynomialCalculator calculator;

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        MarketPrices marketPrices = settledBet.getBetAction().getMarketPrices();
        double fairness = calculator.getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(Side.BACK))
                .getAsDouble();
        return Collections.singleton(getCategory(fairness));
    }

    String getCategory(double fairness) {
        return PREFIX + Preconditions.checkNotNull(RANGES.get(fairness));
    }
}
