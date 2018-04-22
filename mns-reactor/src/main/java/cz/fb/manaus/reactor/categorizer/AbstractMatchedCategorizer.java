package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Collections;
import java.util.OptionalDouble;
import java.util.Set;

public abstract class AbstractMatchedCategorizer implements SettledBetCategorizer {

    public static final RangeMap<Double, String> CATEGORY_STEPS = ImmutableRangeMap.<Double, String>builder()
            .put(Range.closed(0d, 10d), "0-10")
            .put(Range.openClosed(10d, 100d), "10-100")
            .put(Range.openClosed(100d, 1000d), "100-1k")
            .put(Range.openClosed(1000d, 10_000d), "1k-10k")
            .put(Range.openClosed(10_000d, 100_000d), "10k-100k")
            .put(Range.openClosed(100_000d, 1000_000d), "100k-1M")
            .put(Range.openClosed(1000_000d, 10_000_000d), "1M-10M")
            .put(Range.downTo(10_000_000d, BoundType.OPEN), "10M+")
            .build();

    private final String prefix;

    protected AbstractMatchedCategorizer(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        OptionalDouble amount = getAmount(settledBet);
        return amount.isPresent() ? Collections.singleton(getCategory(amount.getAsDouble())) : Set.of();
    }

    protected abstract OptionalDouble getAmount(SettledBet settledBet);

    String getCategory(double matchedAmount) {
        return prefix + CATEGORY_STEPS.get(matchedAmount);
    }
}
