package cz.fb.manaus.core.category

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.SPORT_PREFIX
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun `market category`() {
        val eventType = market.eventType
        var market = market

        market = market.copy(eventType = eventType.copy(name = "Soccer"))
        assertThat(categoryService.getMarketCategories(market, false), hasItem(SPORT_SOCCER))

        market = market.copy(eventType = eventType.copy(name = "Tennis"))
        assertThat(categoryService.getMarketCategories(market, false), hasItem(SPORT_TENNIS))

        market = market.copy(eventType = eventType.copy(name = "Golf"))
        assertThat(categoryService.getMarketCategories(market, false),
                hasItem(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GOLF))
    }

    @Test
    fun `bet category`() {
        val categories = categoryService.getRealizedBetCategories(realizedBet, false, BetCoverage.EMPTY)
        assertThat(categories,
                hasItems("market_country_cz", "market_sport_soccer", "market_type_match_odds"))
    }

    companion object {
        const val SPORT_SOCCER = Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.SOCCER
        const val SPORT_TENNIS = Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.TENNIS
    }
}
