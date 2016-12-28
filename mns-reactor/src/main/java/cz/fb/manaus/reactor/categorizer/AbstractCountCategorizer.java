package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Collections;
import java.util.Set;

abstract public class AbstractCountCategorizer implements SettledBetCategorizer {

    private final String prefix;
    private final int maxCount;

    protected AbstractCountCategorizer(String prefix, int maxCount) {
        this.prefix = prefix;
        this.maxCount = maxCount;
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        int count = getCount(settledBet);
        return Collections.singleton(prefix + toCategory(count));
    }

    protected abstract int getCount(SettledBet bet);

    private String toCategory(int count) {
        if (count >= maxCount) {
            return maxCount + "+";
        } else {
            return Integer.toString(count);
        }
    }
}
