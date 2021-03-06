package cz.fb.manaus.core.category

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import cz.fb.manaus.core.category.categorizer.SPORT_PREFIX
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractTestCase5
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoryServiceRealDataTest : AbstractTestCase5() {
    private lateinit var markets: List<Market>

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @BeforeEach
    fun setUp() {
        val resource = resourceLoader.getResource("classpath:cz/fb/manaus/core/service/markets.json")
        markets = objectMapper.readValue(resource.inputStream, object : TypeReference<List<Market>>() {})
        markets = markets.map { it.copy(event = it.event.copy(openDate = Instant.now().plus(5, ChronoUnit.HOURS))) }
    }

    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun testDisjunctiveCategories() {
        for (market in markets) {
            val marketCategories = categoryService.getMarketCategories(market, false)
            val intersection = marketCategories intersect DISJUNCTIVE_CATEGORIES
            assertEquals(1, intersection.size, market.toString())
        }
    }

    @Test
    fun testSoccer() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.SOCCER, "soccer")
    }

    @Test
    fun testBasket() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.BASKETBALL, "basket")
    }

    @Test
    fun testTennis() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.TENNIS, "tennis")
    }

    @Test
    fun testAmericanFootball() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.AMERICAN_FOOTBALL, "american")
    }

    @Test
    fun testIceHockey() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.ICE_HOCKEY, "hockey")
    }

    @Test
    fun testGreyhounds() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GREY_HOUNDS, "hound")
    }

    @Test
    fun testVolleyball() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.VOLLEYBALL, "volley")
    }

    @Test
    fun testGolf() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GOLF, "golf")
    }

    @Test
    fun testRunnerCount() {
        checkCategory(Category.MARKET_PREFIX + "runnerCount_" + "4", null)
    }

    @Test
    fun testUkraine() {
        checkCategory(Category.MARKET_PREFIX + COUNTRY_PREFIX + "ua", null)
    }

    @Test
    fun testFinancialBets() {
        checkCategory(Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.FINANCIAL, "financial")
    }

    private fun checkCategory(category: String, mustContainLower: String?) {
        val size = markets.size
        val count = getCategoryCount(category, mustContainLower)
        assertTrue { count in 1 until size }
    }

    private fun getCategoryCount(category: String, mustContainLower: String?): Int {
        val counts = mutableMapOf<String, Int>().withDefault { 0 }
        for (market in markets) {
            val categories = categoryService.getMarketCategories(market, false)
            categories.forEach { counts[it] = counts.getValue(it) + 1 }
            if (category in categories) {
                if (mustContainLower != null) {
                    assertTrue { mustContainLower in market.eventType.name.lowercase() }
                }
            }
        }
        return counts[category] ?: error("no such category")
    }

    companion object {
        val DISJUNCTIVE_CATEGORIES = setOf(
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.BASKETBALL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.VOLLEYBALL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.BASEBALL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.SOCCER,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.AMERICAN_FOOTBALL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GOLF,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.HANDBALL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.GREY_HOUNDS,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.TENNIS,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.FINANCIAL,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.SNOOKER,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.CRICKET,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.RUGBY,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.ICE_HOCKEY,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.MOTOR_SPORT,
            Category.MARKET_PREFIX + SPORT_PREFIX + MarketCategories.HORSES
        )
    }
}
