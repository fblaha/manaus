package cz.fb.manaus.reactor.categorizer.namespace;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class LastMatchedCategorizer implements SettledBetCategorizer {

    public static final String NAMESPACE = "lastMatched";

    @Override
    public Optional<String> getNamespace() {
        return Optional.of(NAMESPACE);
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        RunnerPrices runnerPrices = settledBet.getBetAction().getMarketPrices().getRunnerPrices(settledBet.getSelectionId());
        return singleton(NAMESPACE + "_" + runnerPrices.getLastMatchedPrice());
    }
}
