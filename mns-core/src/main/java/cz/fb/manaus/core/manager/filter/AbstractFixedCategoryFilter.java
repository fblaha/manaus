package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public abstract class AbstractFixedCategoryFilter implements MarketFilter {
    private final Set<Set<String>> excludedCategories;
    @Autowired
    private CategoryService categoryService;

    public AbstractFixedCategoryFilter(Set<Set<String>> excludedCategories) {
        this.excludedCategories = excludedCategories;
    }

    @Override
    public boolean test(Market market) {
        Set<String> categories = categoryService.getMarketCategories(market, false);
        for (Set<String> excluded : excludedCategories) {
            if (categories.containsAll(excluded)) {
                return false;
            }
        }
        return true;
    }
}
