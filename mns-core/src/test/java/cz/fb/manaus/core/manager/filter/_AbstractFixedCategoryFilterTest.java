package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.Category;
import cz.fb.manaus.core.category.categorizer.SportCategorizer;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.service.AbstractMarketDataAwareTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class _AbstractFixedCategoryFilterTest extends AbstractMarketDataAwareTestCase {

    @Autowired
    private TestFilter testFilter;

    @Test
    public void testAccept() throws Exception {
        int size = markets.size();
        int cnt = 0;
        for (Market market : markets) {
            if (testFilter.test(market)) cnt++;
        }
        System.out.println("cnt = " + cnt);
        assertThat(cnt > 0 && cnt < size, is(true));
    }

    @Component
    private static class TestFilter extends AbstractFixedCategoryFilter {

        public static final Set<Set<String>> EXCLUDED_CATEGORIES = Set.of(
                singleton(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HORSES),
                singleton(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SPECIAL),
                singleton(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.FINANCIAL),
                singleton(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.POLITICS));

        public TestFilter() {
            super(EXCLUDED_CATEGORIES);
        }
    }


}
