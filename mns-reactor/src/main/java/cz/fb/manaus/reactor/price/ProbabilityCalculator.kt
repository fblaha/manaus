package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import java.util.*

@Component
object ProbabilityCalculator {

    fun fromFairness(fairness: Double, side: Side, prices: List<RunnerPrices>): Map<Long, Double> {
        val sidePrices = prices.map { it.getHomogeneous(side) }

        val result = HashMap<Long, Double>()
        for (runnerPrice in sidePrices) {
            val bestPrice = runnerPrice.bestPrice
            val unfairPrice = bestPrice!!.price
            val fairPrice = Pricing.getFairnessFairPrice(unfairPrice, fairness)
            result[runnerPrice.selectionId] = 1 / fairPrice
        }
        return result
    }

}
