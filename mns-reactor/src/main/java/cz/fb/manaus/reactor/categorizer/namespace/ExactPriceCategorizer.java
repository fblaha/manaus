package cz.fb.manaus.reactor.categorizer.namespace;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class ExactPriceCategorizer implements SettledBetCategorizer {

    public static final String NAMESPACE = "exactPrice";

    @Override
    public Optional<String> getNamespace() {
        return Optional.of(NAMESPACE);
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        return singleton(NAMESPACE + "_" + settledBet.getPrice().getPrice());
    }
}
