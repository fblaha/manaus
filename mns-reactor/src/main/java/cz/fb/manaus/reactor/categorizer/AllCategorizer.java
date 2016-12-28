package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class AllCategorizer implements SettledBetCategorizer {

    public static final Set<String> CATEGORIES = singleton(MarketCategories.ALL);

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        return CATEGORIES;
    }
}
