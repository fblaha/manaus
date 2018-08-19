package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ProbabilityCalculatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var factory: ReactorTestFactory
    @Autowired
    private lateinit var calculator: ProbabilityCalculator
    @Autowired
    private lateinit var fairnessPolynomialCalculator: FairnessPolynomialCalculator

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
            val prices = factory.createMarket(rate, probabilities)
            val fairness = fairnessPolynomialCalculator.getFairness(prices)
            for (side in Side.values()) {
                val probability = calculator.fromFairness(fairness.get(side).asDouble, side, prices)
                for (i in probabilities.indices) {
                    val expected = probabilities[i]
                    val selection = CoreTestFactory.HOME + i
                    Assert.assertEquals(expected, probability[selection]!!, 0.005)
                }
            }
        }
    }
}