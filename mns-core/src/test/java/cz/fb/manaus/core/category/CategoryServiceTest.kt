package cz.fb.manaus.core.category

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.SportCategorizer
import cz.fb.manaus.core.model.EventType
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var categoryService: CategoryService
    private lateinit var market: Market
    private lateinit var eventType: EventType

    @Before
    fun createMarket() {
        market = CoreTestFactory.newTestMarket()
        eventType = EventType("1", "Y")
        market.eventType = eventType
    }

    @Test
    fun testCategory() {
        eventType.name = "Soccer"
        var categories = categoryService.getMarketCategories(market, false)
        assertThat(categories, hasItem(SPORT_SOCCER))

        eventType.name = "Tennis"
        categories = categoryService.getMarketCategories(market, false)
        assertThat(categories, hasItem(SPORT_TENNIS))

        eventType.name = "Horse Racing"
        categories = categoryService.getMarketCategories(market, false)
        assertThat(categories, hasItem(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HORSES))

        eventType.name = "Golf"
        categories = categoryService.getMarketCategories(market, false)
        assertThat(categories, hasItem(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF))
    }


    @Test
    fun testBetCategory() {
        val categories = categoryService.getSettledBetCategories(
                CoreTestFactory.newSettledBet(2.0, Side.LAY), false, BetCoverage.EMPTY)
        assertThat(categories,
                hasItems("market_country_br", "market_runnerCount_3", "market_sport_soccer", "market_type_match_odds"))
    }

    companion object {
        const val SPORT_SOCCER = Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER
        const val SPORT_TENNIS = Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS
    }
}
