package cz.fb.manaus.reactor.price

import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceServiceTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var priceService: PriceService
    @Autowired
    private lateinit var factory: ReactorTestFactory
    @Autowired
    private lateinit var provider: ExchangeProvider
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

    private fun checkDownGrade(newPrice: Double, oldPrice: Double, type: Side) {
        val newOne = Price(newPrice, 10.0, type)
        val oldOne = Price(oldPrice, 10.0, type)
        assertTrue(isDowngrade(newOne, oldOne))
        assertFalse(isDowngrade(oldOne, newOne))
    }

    private fun isDowngrade(newOne: Price, oldOne: Price): Boolean {
        val newPrice = newOne.price
        val oldPrice = oldOne.price
        return priceService.isDowngrade(newPrice, oldPrice, newOne.side)
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

        assertEquals(5.0, priceService.getRoundedFairnessFairPrice(4.2, backFairness)!!, 0.01)
        assertEquals(3.35, priceService.getRoundedFairnessFairPrice(2.87, backFairness)!!, 0.01)
        assertEquals(2.0, priceService.getRoundedFairnessFairPrice(1.8, backFairness)!!, 0.01)
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
        (1..9).forEach { i -> checkFairPrices(1, 1.9, 1.0 + i * 0.1) }
    }

    @Test
    fun `overround fair price 3 runners - generated`() {
        (1..19).forEach { i ->
            val price = 1.0 + i * 0.1
            checkFairPrices(1, 2.8, 2.8, price)
        }
        (1..19).forEach { i ->
            val price = 1.0 + i * 0.1
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
        val marketPrices = newMarketPrices(Doubles.asList(*unfairPrices))
        val overround = getOverround(marketPrices, Side.BACK)!!
        val reciprocal = getReciprocal(marketPrices, Side.BACK)!!
        val fairness = getFairness(Side.BACK, marketPrices)
        assertTrue(overround > 1)

        val overroundPrices = unfairPrices
                .map { price ->
                    val fair = priceService.getOverroundFairPrice(price, overround,
                            unfairPrices.size)
                    assertTrue(price < fair)
                    fair
                }

        checkOverroundUnfairPrices(reciprocal, winnerCount, Doubles.asList(*unfairPrices), overroundPrices)

        val fairnessPrices = unfairPrices.map { price ->
            val fair = priceService.getFairnessFairPrice(price, fairness)
            assertTrue(price < fair)
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
        return Math.max(1 / (selectionOverround + probability), provider.minPrice)
    }
}
