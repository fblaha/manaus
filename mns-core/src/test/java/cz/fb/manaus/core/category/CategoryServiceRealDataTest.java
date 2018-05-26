package cz.fb.manaus.core.category;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Sets;
import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer;
import cz.fb.manaus.core.category.categorizer.RunnerCountCategorizer;
import cz.fb.manaus.core.category.categorizer.SportCategorizer;
import cz.fb.manaus.core.manager.AbstractMarketDataAwareTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CategoryServiceRealDataTest extends AbstractMarketDataAwareTestCase {
    public static final Set<String> DISJUNCTIVE_CATEGORIES = Set.of(
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
    public void testDisjunctiveCategories() {
        for (var market : markets) {
            var marketCategories = categoryService.getMarketCategories(market, false);
            var intersection = Sets.intersection(marketCategories, DISJUNCTIVE_CATEGORIES);
            assertThat(market.toString(), intersection.size(), is(1));
        }
    }

    @Test
    public void testSoccer() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER, "soccer");
    }

    @Test
    public void testBasket() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.BASKETBALL, "basket");
    }

    @Test
    public void testTennis() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS, "tennis");
    }

    @Test
    public void testAmericanFootball() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.AMERICAN_FOOTBALL, "american");
    }

    @Test
    public void testIceHockey() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.ICE_HOCKEY, "hockey");
    }

    @Test
    public void testGreyhounds() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GREY_HOUNDS, "hound");
    }

    @Test
    public void testVolleyball() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.VOLLEYBALL, "volley");
    }

    @Test
    public void testGolf() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF, "golf");
    }

    @Test
    public void testRunnerCount() {
        checkCategory(Category.MARKET_PREFIX + RunnerCountCategorizer.PREFIX + "4", null);
    }

    @Test
    public void testUkraine() {
        checkCategory(Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX + "ua", null);
    }

    @Test
    public void testFinancialBets() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.FINANCIAL, "financial");
    }

    private void checkCategory(String category, String mustContainLower) {
        var size = markets.size();
        var count = getCategoryCount(category, mustContainLower);
        assertTrue(count > 0 && count < size);
    }

    private int getCategoryCount(String category, String mustContainLower) {
        var counts = HashMultiset.<String>create();
        for (var market : markets) {
            var categories = categoryService.getMarketCategories(market, false);
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
