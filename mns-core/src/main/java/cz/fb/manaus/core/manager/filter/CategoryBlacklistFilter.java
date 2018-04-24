package cz.fb.manaus.core.manager.filter;

import com.google.common.collect.Sets;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CategoryBlacklistFilter implements MarketFilter {

    private static final Logger log = Logger.getLogger(CategoryBlacklistFilter.class.getSimpleName());

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean accept(Market market, Set<String> blacklist) {
        Set<String> categories = categoryService.getMarketCategories(market, false);
        Sets.SetView<String> intersection = Sets.intersection(categories, blacklist);
        if (!intersection.isEmpty()) {
            log.log(Level.INFO, "blacklist category ''{0}'' for market ''{1}''",
                    new Object[]{intersection, market});
        }
        return intersection.isEmpty();
    }
}