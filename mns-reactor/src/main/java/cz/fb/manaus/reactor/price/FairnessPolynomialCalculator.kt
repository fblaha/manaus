package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.Side
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.apache.commons.math3.analysis.solvers.LaguerreSolver
import org.apache.commons.math3.exception.NoBracketingException
import org.springframework.stereotype.Component
import java.util.*

@Component
class FairnessPolynomialCalculator {

    private fun toPolynomial(price: Double): PolynomialFunction {
        return PolynomialFunction(doubleArrayOf(1.0, price - 1))
    }

    fun getFairness(winnerCount: Double, prices: List<Double?>): Double? {
        if (prices.all { it != null }) {
            val presentPrices = prices.mapNotNull { it }
            var rightSide = multiplyPolynomials(presentPrices)
            rightSide = rightSide.multiply(PolynomialFunction(doubleArrayOf(winnerCount)))

            val leftSideItems = LinkedList<PolynomialFunction>()
            for (i in presentPrices.indices) {
                val otherPrices = LinkedList<Double>()
                for (j in presentPrices.indices) {
                    if (i != j) {
                        otherPrices.add(presentPrices[j])
                    }
                }
                leftSideItems.add(multiplyPolynomials(otherPrices))
            }
            val leftSide = leftSideItems.reduce { obj, p -> obj.add(p) }
            val equation = leftSide.subtract(rightSide)

            val laguerreSolver = LaguerreSolver()
            return try {
                val root = laguerreSolver.solve(100, equation, 0.0, 1000.0)
                1 / root
            } catch (exception: NoBracketingException) {
                null
            }
        } else {
            return null
        }
    }

    fun getFairness(marketPrices: MarketPrices): Fairness {
        return Fairness(
                getFairness(marketPrices.winnerCount.toDouble(), Fairness.toKotlin(marketPrices.getBestPrices(Side.BACK))),
                getFairness(marketPrices.winnerCount.toDouble(), Fairness.toKotlin(marketPrices.getBestPrices(Side.LAY))))
    }

    private fun multiplyPolynomials(prices: List<Double>): PolynomialFunction {
        val polynomials = prices.map { this.toPolynomial(it) }
        return polynomials.reduce { obj, p -> obj.multiply(p) }
    }

}
