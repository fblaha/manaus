package cz.fb.manaus.core.category

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.SPORT_PREFIX
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class CategoryServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun `market category`() {
        val eventType = market.eventType
        var market = market

        market = market.copy(eventType = eventType.copy(name = "Soccer"))
        assertTrue { SPORT_SOCCER in categoryService.getMarketCategories(market, false) }

        market = market.copy(eventType = eventType.copy(name = "Tennis"))
        assertTrue { SPORT_TENNIS in categoryService.getMarketCategories(market, false) }

        market = market.copy(eventType = eventType.copy(name = "Golf"))
        assertTrue { Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GOLF in categoryService.getMarketCategories(market, false) }
    }

    @Test
    fun `bet category`() {
        val categories = categoryService.getRealizedBetCategories(realizedBet, false)
        assertTrue { categories.containsAll(listOf("market_country_cz", "market_sport_soccer", "market_type_match_odds")) }
    }

    companion object {
        const val SPORT_SOCCER = Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.SOCCER
        const val SPORT_TENNIS = Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.TENNIS
    }
}
