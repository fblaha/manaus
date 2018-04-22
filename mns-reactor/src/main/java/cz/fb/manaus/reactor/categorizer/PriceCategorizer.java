package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.google.common.collect.Range.closedOpen;
import static com.google.common.collect.Range.downTo;
import static com.google.common.collect.Range.upTo;
import static java.util.Objects.requireNonNull;

@Component
public class PriceCategorizer implements SettledBetCategorizer {

    public static final RangeMap<Double, String> CATEGORY_STEPS = ImmutableRangeMap.<Double, String>builder()
            .put(upTo(1.2d, BoundType.OPEN), "1.0-1.2")
            .put(closedOpen(1.2d, 1.5d), "1.2-1.5")
            .put(closedOpen(1.5d, 2d), "1.5-2.0")
            .put(closedOpen(2d, 2.5d), "2.0-2.5")
            .put(closedOpen(2.5d, 3d), "2.5-3.0")
            .put(closedOpen(3d, 4d), "3.0-4.0")
            .put(closedOpen(4d, 5d), "4.0-5.0")
            .put(downTo(5d, BoundType.CLOSED), "5.0+").build();

    String getCategory(double price) {
        String suffix = requireNonNull(CATEGORY_STEPS.get(price));
        return "priceRange_" + suffix;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        String category = getCategory(settledBet.getPrice().getPrice());
        return Set.of(category);
    }
}
