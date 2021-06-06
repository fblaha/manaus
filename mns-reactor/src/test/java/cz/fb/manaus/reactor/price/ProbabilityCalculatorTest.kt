package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.SEL_HOME
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.PricesTestFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProbabilityCalculatorTest {

    @Test
    fun `probability calculated from fairness`() {
        checkProbability(listOf(0.6, 0.25, 0.15))
        checkProbability(listOf(0.4, 0.3, 0.3))
        checkProbability(listOf(0.6, 0.4))
        checkProbability(listOf(0.6, 0.2, 0.1, 0.1))
        checkProbability(listOf(0.9, 0.1))
    }

    private fun checkProbability(probabilities: List<Double>) {
        val rates = listOf(0.05, 0.1, 0.2, 0.4)
        for (rate in rates) {
            val prices = PricesTestFactory.newMarketPrices(rate, probabilities)
            val fairness = FairnessPolynomialCalculator.getFairness(prices)
            for (side in Side.values()) {
                val probability = ProbabilityCalculator.fromFairness(fairness[side]!!, side, prices)
                for ((i, expected) in probabilities.withIndex()) {
                    val selection = SEL_HOME * (i + 1)
                    assertEquals(expected, probability.getValue(selection), 0.005)
                }
            }
        }
    }
}