package cz.fb.manaus.core.category;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.category.categorizer.SportCategorizer;
import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class CategoryServiceTest extends AbstractLocalTestCase {
    public static final String SPORT_SOCCER = Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER;
    public static final String SPORT_TENNIS = Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS;
    @Autowired
    private CategoryService categoryService;
    private Market market;
    private EventType eventType;

    @Before
    public void createMarket() {
        market = CoreTestFactory.newMarket();
        eventType = new EventType("1", "Y");
        market.setEventType(eventType);
    }

    @Test
    public void testCategory() throws Exception {
        eventType.setName("Soccer");
        Set<String> categories = categoryService.getMarketCategories(market, false);
        assertThat(categories, hasItem(SPORT_SOCCER));

        eventType.setName("Tennis");
        categories = categoryService.getMarketCategories(market, false);
        assertThat(categories, hasItem(SPORT_TENNIS));

        eventType.setName("Horse Racing");
        categories = categoryService.getMarketCategories(market, false);
        assertThat(categories, hasItem(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HORSES));

        eventType.setName("Golf");
        categories = categoryService.getMarketCategories(market, false);
        assertThat(categories, hasItem(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF));
    }


    @Test
    public void testBetCategory() throws Exception {
        Set<String> categories = categoryService.getSettledBetCategories(
                CoreTestFactory.newSettledBet(2d, Side.LAY), false, BetCoverage.EMPTY);
        assertThat(categories,
                hasItems("market_country_br", "market_runnerCount_3", "market_sport_soccer", "market_type_match_odds"));
    }
}
