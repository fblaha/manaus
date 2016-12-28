package cz.fb.manaus.reactor.categorizer.namespace;

import com.google.common.math.DoubleMath;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;

public abstract class AbstractDetailMatchedCategorizer implements SettledBetCategorizer {

    private final String namespace;

    protected AbstractDetailMatchedCategorizer(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        double matchedAmount = getMatchedAmount(settledBet);
        int amount = DoubleMath.roundToInt(matchedAmount, RoundingMode.HALF_UP);
        if (amount > 1000) return Collections.emptySet();
        return singleton(namespace + "_" + amount);
    }

    protected abstract double getMatchedAmount(SettledBet settledBet);
}
