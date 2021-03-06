package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.price.Pricing.downgrade
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class PricingTest {

    @Test
    fun `price downgrading`() {
        val eps = 0.000001
        assertEquals(3.0, downgrade(3.0, 0.0, Side.BACK), eps)
        assertEquals(3.0, downgrade(3.0, 0.0, Side.LAY), eps)
        assertEquals(1.0, downgrade(3.0, 1.0, Side.LAY), eps)
        assertEquals(2.0, downgrade(3.0, 0.5, Side.LAY), eps)
        assertEquals(5.0, downgrade(3.0, 0.5, Side.BACK), eps)
        assertEquals(3.88, downgrade(4.0, 0.04, Side.LAY), eps)
        assertEquals(4.125, downgrade(4.0, 0.04, Side.BACK), eps)
    }

    @Test
    fun `is downgrade`() {
        checkDownGrade(2.0, 3.0, Side.LAY)
        checkDownGrade(3.0, 2.0, Side.BACK)
    }

    private fun checkDownGrade(newPrice: Double, oldPrice: Double, side: Side) {
        assertTrue(Pricing.isDowngrade(newPrice, oldPrice, side))
        assertFalse(Pricing.isDowngrade(oldPrice, newPrice, side))
    }

    private fun getFairness(side: Side, marketPrices: List<RunnerPrices>): Double {
        return FairnessPolynomialCalculator.getFairness(1, getBestPrices(marketPrices, side))!!
    }

    @Test
    fun `fair price`() {
        val marketPrices = listOf(
            PricesTestFactory.newRunnerPrices(1, 4.2, 6.0),
            PricesTestFactory.newRunnerPrices(2, 2.87, 4.0),
            PricesTestFactory.newRunnerPrices(1, 1.8, 3.0)
        )
        val layFairness = getFairness(Side.LAY, marketPrices)
        assertEquals(1.5, layFairness, 0.1)
        val backFairness = getFairness(Side.BACK, marketPrices)
        assertEquals(0.8, backFairness, 0.001)

        assertEquals(5.0, getRoundedFairnessFairPrice(4.2, backFairness), 0.01)
        assertEquals(3.336, getRoundedFairnessFairPrice(2.87, backFairness), 0.01)
        assertEquals(2.0, getRoundedFairnessFairPrice(1.8, backFairness), 0.01)
    }

    private fun getRoundedFairnessFairPrice(unfairPrice: Double, fairness: Double): Double {
        val fairPrice = Pricing.getFairnessFairPrice(unfairPrice, fairness)
        return Price.round(fairPrice)
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

    private fun newMarketPrices(unfairPrices: List<Double>): List<RunnerPrices> {
        val runnerPrices = mutableListOf<RunnerPrices>()
        for ((i, unfairPrice) in unfairPrices.withIndex()) {
            runnerPrices.add(PricesTestFactory.newRunnerPrices(i.toLong(), unfairPrice, 10.0))
        }
        return runnerPrices
    }

    private fun checkFairPrices(winnerCount: Int, vararg unfairPrices: Double) {
        val marketPrices = newMarketPrices(unfairPrices.asList())
        val overround = getOverRound(marketPrices, Side.BACK)!!
        val reciprocal = getReciprocal(marketPrices, Side.BACK)!!
        val fairness = getFairness(Side.BACK, marketPrices)
        assertTrue { overround > 1 }

        val overroundPrices = unfairPrices.map {
            val fair = Pricing.getOverRoundFairPrice(
                it, overround,
                unfairPrices.size
            )
            assertTrue { it < fair }
            fair
        }

        checkOverroundUnfairPrices(reciprocal, winnerCount, unfairPrices.asList(), overroundPrices)

        val fairnessPrices = unfairPrices.map {
            val fair = Pricing.getFairnessFairPrice(it, fairness)
            assertTrue(it < fair)
            fair
        }

        val overFairnessBased = getOverRound(newMarketPrices(fairnessPrices), Side.BACK)
        val overOverRoundBased = getOverRound(newMarketPrices(overroundPrices), Side.BACK)

        assertEquals(winnerCount.toDouble(), overFairnessBased!!, 0.001)
        assertEquals(winnerCount.toDouble(), overOverRoundBased!!, 0.001)
    }

    private fun checkOverroundUnfairPrices(
        reciprocal: Double,
        winnerCount: Int,
        unfairPrices: List<Double>,
        fairPrices: List<Double>
    ) {
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
        val lowFairPrice = Pricing.getFairnessFairPrice(lowPrice, fairness)
        val highFairPrice = Pricing.getFairnessFairPrice(highPrice, fairness)
        assertTrue(highPrice < highFairPrice)
        assertTrue(lowPrice < lowFairPrice)
    }

    @Test
    fun `fairness based fair prices`() {
        val market = PricesTestFactory.newMarketPrices(0.2, listOf(0.85, 0.1, 0.05))
        val fairness = FairnessPolynomialCalculator.getFairness(market)
        val bestBack = getBestPrices(market, Side.BACK)[0]!!
        val bestLay = getBestPrices(market, Side.LAY)[0]!!
        val fairnessBackFairPrice = Pricing.getFairnessFairPrice(bestBack, fairness[Side.BACK]!!)
        val fairnessLayFairPrice = Pricing.getFairnessFairPrice(bestLay, fairness[Side.LAY]!!)
        assertEquals(fairnessBackFairPrice, fairnessLayFairPrice, 0.01)
    }

    private fun getOverroundUnfairPrice(
        fairPrice: Double,
        targetReciprocal: Double,
        winnerCount: Int,
        runnerCount: Int
    ): Double {
        val overround = winnerCount / targetReciprocal
        val selectionOverround = (overround - winnerCount) / runnerCount
        val probability = 1 / fairPrice
        return max(1 / (selectionOverround + probability), bfProvider.minPrice)
    }
}
