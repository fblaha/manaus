package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Component
public class FineGrainedPriceCategorizer implements SettledBetCategorizer {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.0#");
    public static final RangeMap<Double, String> CATEGORY_STEPS;
    public static final String PRICE_RANGE_FINE = "priceRangeFine_";

    static {
        ImmutableRangeMap.Builder<Double, String> builder = ImmutableRangeMap.builder();
        generateSteps(builder, 1d, 3d, 0.1d);
        generateSteps(builder, 3d, 7d, 0.2d);
        builder.put(Range.downTo(7d, BoundType.CLOSED), FORMAT.format(7d) + "+");
        CATEGORY_STEPS = builder.build();
    }

    private static void generateSteps(ImmutableRangeMap.Builder<Double, String> builder, double lower, double upper, double inc) {
        for (double step = lower; step < upper; step += inc) {
            step = Precision.round(step, 1);
            double up = Precision.round(step + inc, 1);
            builder.put(Range.closedOpen(step, up), FORMAT.format(step) + "-" + FORMAT.format(up));
        }
    }

    private String getCategory(double price) {
        String value = requireNonNull(CATEGORY_STEPS.get(price));
        return PRICE_RANGE_FINE + value;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        String category = getCategory(settledBet.getPrice().getPrice());
        return Set.of(category);
    }
}
