package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Set;

public interface SettledBetCategorizer extends SimulationAware {

    default boolean isMarketSnapshotRequired() {
        return false;
    }

    Set<String> getCategories(SettledBet settledBet, BetCoverage coverage);

}
