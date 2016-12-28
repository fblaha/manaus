package cz.fb.manaus.core.category;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import cz.fb.manaus.core.category.categorizer.RunnerCountCategorizer;
import cz.fb.manaus.core.category.categorizer.SportCategorizer;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.service.AbstractMarketDataAwareTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CategoryServiceRealDataTest extends AbstractMarketDataAwareTestCase {
    public static final Set<String> DISJUNCTIVE_CATEGORIES = ImmutableSet.of(
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.BASKETBALL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.VOLLEYBALL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.BASEBALL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.AMERICAN_FOOTBALL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HANDBALL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GREY_HOUNDS,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.FINANCIAL,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SNOOKER,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.CRICKET,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.RUGBY,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.ICE_HOCKEY,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.MOTOR_SPORT,
            Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HORSES);

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testDisjunctiveCategories() throws Exception {
        for (Market market : markets) {
            Set<String> marketCategories = categoryService.getMarketCategories(market, false, Optional.empty());
            Sets.SetView<String> intersection = Sets.intersection(marketCategories, DISJUNCTIVE_CATEGORIES);
            assertThat(market.toString(), intersection.size(), is(1));
        }
    }

    @Test
    public void testSoccer() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER, "soccer");
    }

    @Test
    public void testBasket() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.BASKETBALL, "basket");
    }

    @Test
    public void testTennis() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS, "tennis");
    }

    @Test
    public void testAmericanFootball() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.AMERICAN_FOOTBALL, "american");
    }

    @Test
    public void testIceHockey() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.ICE_HOCKEY, "hockey");
    }

    @Test
    public void testGreyhounds() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GREY_HOUNDS, "hound");
    }

    @Test
    public void testVolleyball() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.VOLLEYBALL, "volley");
    }

    @Test
    public void testGolf() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF, "golf");
    }

    @Test
    public void testRunnerCount() throws Exception {
        checkCategory(Category.MARKET_PREFIX + RunnerCountCategorizer.PREFIX + "4", null);
    }

    @Test
    public void testUkraine() throws Exception {
        checkCategory(Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX + "ua", null);
    }

    @Test
    public void testFinancialBets() throws Exception {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.FINANCIAL, "financial");
    }

    private void checkCategory(String category, String mustContainLower) {
        int size = Iterables.size(markets);
        int count = getCategoryCount(category, mustContainLower);
        System.out.println("size = " + size);
        System.out.println("count = " + count);
        assertTrue(count > 0 && count < size);
    }

    private int getCategoryCount(String category, String mustContainLower) {
        Multiset<String> counts = HashMultiset.create();
        for (Market market : markets) {
            Set<String> categories = categoryService.getMarketCategories(market, false, Optional.empty());
            counts.addAll(categories);
            if (categories.contains(category)) {
                if (mustContainLower != null) {
                    assertThat(market.getEventType().getName().toLowerCase(), containsString(mustContainLower));
                }
            }
        }
        return counts.count(category);
    }

}