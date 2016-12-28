package cz.fb.manaus.core.category.categorizer;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.emptySet;

public abstract class AbstractDelegatingCategorizer implements SettledBetCategorizer, Categorizer {

    private final String prefix;


    protected AbstractDelegatingCategorizer(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        return getCategories(settledBet.getBetAction().getMarket());
    }

    @Override
    public Set<String> getCategories(Market market) {
        return getPrefixedCategories(market);
    }

    protected abstract Set<String> getCategoryRaw(Market market);

    private Set<String> getPrefixedCategories(Market market) {
        Set<String> category = getCategoryRaw(market);
        if (category == null) return emptySet();
        return ImmutableSet.copyOf(transform(category, input -> Category.MARKET_PREFIX + prefix + input));
    }

}