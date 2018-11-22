package cz.fb.manaus.core.category

import com.google.common.collect.HashMultiset
import com.google.common.collect.Sets
import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.categorizer.CountryCodeCategorizer
import cz.fb.manaus.core.category.categorizer.RunnerCountCategorizer
import cz.fb.manaus.core.category.categorizer.SportCategorizer
import cz.fb.manaus.core.manager.AbstractMarketDataAwareTestCase
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class CategoryServiceRealDataTest : AbstractMarketDataAwareTestCase() {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun testDisjunctiveCategories() {
        for (market in markets) {
            val marketCategories = categoryService.getMarketCategories(market, false)
            val intersection = Sets.intersection(marketCategories, DISJUNCTIVE_CATEGORIES)
            assertEquals(1, intersection.size, market.toString())
        }
    }

    @Test
    fun testSoccer() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.SOCCER, "soccer")
    }

    @Test
    fun testBasket() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.BASKETBALL, "basket")
    }

    @Test
    fun testTennis() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.TENNIS, "tennis")
    }

    @Test
    fun testAmericanFootball() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.AMERICAN_FOOTBALL, "american")
    }

    @Test
    fun testIceHockey() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.ICE_HOCKEY, "hockey")
    }

    @Test
    fun testGreyhounds() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GREY_HOUNDS, "hound")
    }

    @Test
    fun testVolleyball() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.VOLLEYBALL, "volley")
    }

    @Test
    fun testGolf() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.GOLF, "golf")
    }

    @Test
    fun testRunnerCount() {
        checkCategory(Category.MARKET_PREFIX + RunnerCountCategorizer.PREFIX + "4", null)
    }

    @Test
    fun testUkraine() {
        checkCategory(Category.MARKET_PREFIX + CountryCodeCategorizer.PREFIX + "ua", null)
    }

    @Test
    fun testFinancialBets() {
        checkCategory(Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.FINANCIAL, "financial")
    }

    private fun checkCategory(category: String, mustContainLower: String?) {
        val size = markets.size
        val count = getCategoryCount(category, mustContainLower)
        assertTrue(count in 1..(size - 1))
    }

    private fun getCategoryCount(category: String, mustContainLower: String?): Int {
        val counts = HashMultiset.create<String>()
        for (market in markets) {
            val categories = categoryService.getMarketCategories(market, false)
            counts.addAll(categories)
            if (category in categories) {
                if (mustContainLower != null) {
                    assertThat(market.eventType.name.toLowerCase(), containsString(mustContainLower))
                }
            }
        }
        return counts.count(category)
    }

    companion object {
        val DISJUNCTIVE_CATEGORIES = setOf(
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
                Category.MARKET_PREFIX + SportCategorizer.PREFIX + MarketCategories.HORSES)
    }

}
