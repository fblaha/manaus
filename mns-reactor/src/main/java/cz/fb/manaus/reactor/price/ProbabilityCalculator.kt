package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.Side
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProbabilityCalculator {

    @Autowired
    private lateinit var priceService: PriceService

    fun fromFairness(fairness: Double, side: Side, prices: MarketPrices): Map<Long, Double> {
        val sidePrices = prices.getHomogeneous(side)
        val runnerPrices = sidePrices.runnerPrices

        val result = HashMap<Long, Double>()
        for (runnerPrice in runnerPrices) {
            val bestPrice = runnerPrice.bestPrice
            val unfairPrice = bestPrice.get().price
            val fairPrice = priceService.getFairnessFairPrice(unfairPrice, fairness)
            result[runnerPrice.selectionId] = 1 / fairPrice
        }
        return result
    }

}
