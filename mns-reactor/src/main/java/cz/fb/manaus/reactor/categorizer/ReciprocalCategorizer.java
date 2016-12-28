package cz.fb.manaus.reactor.categorizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

import static com.google.common.collect.Range.closedOpen;
import static com.google.common.collect.Range.upTo;

@Component
public class ReciprocalCategorizer implements SettledBetCategorizer {

    public static final RangeMap<Double, String> RANGES = ImmutableRangeMap.<Double, String>builder()
            .put(Range.downTo(1d, BoundType.CLOSED), "1.00+")
            .put(Range.singleton(0.99d), "0.99")
            .put(Range.singleton(0.98d), "0.98")
            .put(Range.singleton(0.97d), "0.97")
            .put(Range.singleton(0.96d), "0.96")
            .put(Range.singleton(0.95d), "0.95")
            .put(Range.singleton(0.94d), "0.94")
            .put(Range.singleton(0.93d), "0.93")
            .put(Range.singleton(0.92d), "0.92")
            .put(Range.singleton(0.91d), "0.91")
            .put(Range.singleton(0.90d), "0.90")
            .put(closedOpen(0.85d, 0.90d), "0.85-0.90")
            .put(closedOpen(0.80d, 0.85d), "0.80-0.85")
            .put(closedOpen(0.75d, 0.80d), "0.75-0.80")
            .put(closedOpen(0.70d, 0.75d), "0.70-0.75")
            .put(closedOpen(0.60d, 0.70d), "0.60-0.70")
            .put(closedOpen(0.50d, 0.60d), "0.50-0.60")
            .put(closedOpen(0.30d, 0.50d), "0.30-0.50")
            .put(upTo(0.30d, BoundType.OPEN), "0.00-0.30")
            .build();

    public static final String RECIPROCAL = "reciprocal_";

    @Autowired(required = false)
    private CustomReciprocalRangeSupplier customReciprocalRangeSupplier;

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    private void handleCustomRange(double reciprocal, Set<String> result) {
        if (customReciprocalRangeSupplier != null) {
            String customRange = customReciprocalRangeSupplier.getCustomRanges().get(reciprocal);
            if (customRange != null) {
                result.add(RECIPROCAL + customRange);
            }
        }
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        OptionalDouble reciprocal = settledBet.getBetAction().getMarketPrices().getReciprocal(Side.BACK);
        if (reciprocal.isPresent()) {
            Set<String> result = new HashSet<>();
            handleCustomRange(reciprocal.getAsDouble(), result);
            double rounded = Precision.round(reciprocal.getAsDouble(), 2);
            String strRange = Preconditions.checkNotNull(RANGES.get(rounded), reciprocal.getAsDouble());
            result.add(RECIPROCAL + strRange);
            return result;
        }
        return Collections.emptySet();
    }
}
