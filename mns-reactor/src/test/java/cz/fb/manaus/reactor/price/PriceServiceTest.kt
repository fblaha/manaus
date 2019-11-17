package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.rounding.RoundingService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.math.max
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceServiceTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var priceService: PriceService
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: PricesTestFactory
    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    @Test
    fun `price downgrading`() {
        assertEquals(3.0, priceService.downgrade(3.0, 0.0, Side.BACK), 0.000001)
        assertEquals(3.0, priceService.downgrade(3.0, 0.0, Side.LAY), 0.000001)
        assertEquals(1.0, priceService.downgrade(3.0, 1.0, Side.LAY), 0.000001)
        assertEquals(2.0, priceService.downgrade(3.0, 0.5, Side.LAY), 0.000001)
        assertEquals(5.0, priceService.downgrade(3.0, 0.5, Side.BACK), 0.000001)
        assertEquals(3.88, priceService.downgrade(4.0, 0.04, Side.LAY), 0.000001)
        assertEquals(4.125, priceService.downgrade(4.0, 0.04, Side.BACK), 0.000001)
    }

    @Test
    fun `is downgrade`() {
        checkDownGrade(2.0, 3.0, Side.LAY)
        checkDownGrade(3.0, 2.0, Side.BACK)
    }

    private fun checkDownGrade(newPrice: Double, oldPrice: Double, side: Side) {
        assertTrue(priceService.isDowngrade(newPrice, oldPrice, side))
        assertFalse(priceService.isDowngrade(oldPrice, newPrice, side))
    }

    private fun getFairness(side: Side, marketPrices: List<RunnerPrices>): Double {
        return calculator.getFairness(1, getBestPrices(marketPrices, side))!!
    }

    @Test
    fun `fair price`() {
        val marketPrices = listOf(factory.newRunnerPrices(1, 4.2, 6.0), factory.newRunnerPrices(2, 2.87, 4.0), factory.newRunnerPrices(1, 1.8, 3.0))
        val layFairness = getFairness(Side.LAY, marketPrices)
        assertEquals(1.5, layFairness, 0.1)
        val backFairness = getFairness(Side.BACK, marketPrices)
        assertEquals(0.8, backFairness, 0.001)

        assertEquals(5.0, getRoundedFairnessFairPrice(4.2, backFairness)!!, 0.01)
        assertEquals(3.35, getRoundedFairnessFairPrice(2.87, backFairness)!!, 0.01)
        assertEquals(2.0, getRoundedFairnessFairPrice(1.8, backFairness)!!, 0.01)
    }

    private fun getRoundedFairnessFairPrice(unfairPrice: Double, fairness: Double): Double? {
        val fairPrice = priceService.getFairnessFairPrice(unfairPrice, fairness)
        return roundingService.roundBet(fairPrice, provider::matches)
    }

    @Test
    fun `overround fair price 3 runners`() {
        checkFairPrices(1, 2.5, 3.25, 3.0)
        checkFairPrices(1, 2.7, 2.7, 2.7)
    }

    @Test
    fun `overround fair price 2 runners`() {
        checkFairPrices(1, 1.3, 1.8)
        checkFairPrices(1, 1.3, 2.2)
        checkFairPrices(1, 1.1, 2.7)
        checkFairPrices(1, 1.03, 15.0)
        checkFairPrices(1, 1.03, 1.04)
    }

    @Test
    fun `overround fair price - real football`() {
        checkFairPrices(1, 1.44, 4.1, 6.4)
        checkFairPrices(1, 1.1, 8.0, 15.0)
    }

    @Test
    fun `overround fair price 2 runners - generated`() {
        (1..9).forEach { checkFairPrices(1, 1.9, 1.0 + it * 0.1) }
    }

    @Test
    fun `overround fair price 3 runners - generated`() {
        (1..19).forEach {
            val price = 1.0 + it * 0.1
            checkFairPrices(1, 2.8, 2.8, price)
        }
        (1..19).forEach {
            val price = 1.0 + it * 0.1
            checkFairPrices(1, 2.8, 1.5, price)
        }
    }


    @Test
    fun `overround fair price - basket real`() {
        checkFairPrices(1, 1.34, 17.0, 3.24)
    }

    @Test
    fun `overround fair price - tennis real`() {
        checkFairPrices(1, 1.73, 1.88)
        checkFairPrices(1, 1.09, 1.59)
        checkFairPrices(1, 1.4, 2.6)
    }

    fun newMarketPrices(unfairPrices: List<Double>): List<RunnerPrices> {
        val runnerPrices = mutableListOf<RunnerPrices>()
        for ((i, unfairPrice) in unfairPrices.withIndex()) {
            runnerPrices.add(factory.newRunnerPrices(i.toLong(), unfairPrice, 10.0))
        }
        return runnerPrices
    }

    private fun checkFairPrices(winnerCount: Int, vararg unfairPrices: Double) {
        val marketPrices = newMarketPrices(unfairPrices.asList())
        val overround = getOverround(marketPrices, Side.BACK)!!
        val reciprocal = getReciprocal(marketPrices, Side.BACK)!!
        val fairness = getFairness(Side.BACK, marketPrices)
        assertTrue(overround > 1)

        val overroundPrices = unfairPrices.map {
            val fair = priceService.getOverroundFairPrice(it, overround,
                    unfairPrices.size)
            assertTrue(it < fair)
            fair
        }

        checkOverroundUnfairPrices(reciprocal, winnerCount, unfairPrices.asList(), overroundPrices)

        val fairnessPrices = unfairPrices.map {
            val fair = priceService.getFairnessFairPrice(it, fairness)
            assertTrue(it < fair)
            fair
        }

        val overFairnessBased = getOverround(newMarketPrices(fairnessPrices), Side.BACK)
        val overOverroundBased = getOverround(newMarketPrices(overroundPrices), Side.BACK)

        assertEquals(winnerCount.toDouble(), overFairnessBased!!, 0.001)
        assertEquals(winnerCount.toDouble(), overOverroundBased!!, 0.001)
    }

    private fun checkOverroundUnfairPrices(reciprocal: Double, winnerCount: Int, unfairPrices: List<Double>, fairPrices: List<Double>) {
        for ((unfairOrig, fair) in unfairPrices.zip(fairPrices)) {
            val unfairPrice = getOverroundUnfairPrice(fair, reciprocal, winnerCount, unfairPrices.size)
            assertEquals(unfairOrig, unfairPrice, 0.000001)
        }
    }

    @Test
    fun `fairness - high probability`() {
        val lowPrice = 1.04
        val highPrice = 15.0
        val home = homePrices.copy(prices = listOf(Price(lowPrice, 10.0, Side.BACK)))
        val away = home.copy(selectionId = SEL_AWAY, prices = listOf(Price(highPrice, 10.0, Side.BACK)))
        val fairness = getFairness(Side.BACK, listOf(home, away))
        val lowFairPrice = priceService.getFairnessFairPrice(lowPrice, fairness)
        val highFairPrice = priceService.getFairnessFairPrice(highPrice, fairness)
        assertTrue(highPrice < highFairPrice)
        assertTrue(lowPrice < lowFairPrice)
    }

    @Test
    fun `fairness based fair prices`() {
        val market = factory.newMarketPrices(0.2, listOf(0.85, 0.1, 0.05))
        val fairness = calculator.getFairness(market)
        val bestBack = getBestPrices(market, Side.BACK)[0]!!
        val bestLay = getBestPrices(market, Side.LAY)[0]!!
        val fairnessBackFairPrice = priceService.getFairnessFairPrice(bestBack, fairness[Side.BACK]!!)
        val fairnessLayFairPrice = priceService.getFairnessFairPrice(bestLay, fairness[Side.LAY]!!)
        assertEquals(fairnessBackFairPrice, fairnessLayFairPrice, 0.01)
    }

    private fun getOverroundUnfairPrice(fairPrice: Double, targetReciprocal: Double, winnerCount: Int, runnerCount: Int): Double {
        val overround = winnerCount / targetReciprocal
        val selectionOverround = (overround - winnerCount) / runnerCount
        val probability = 1 / fairPrice
        return max(1 / (selectionOverround + probability), provider.minPrice)
    }
}
