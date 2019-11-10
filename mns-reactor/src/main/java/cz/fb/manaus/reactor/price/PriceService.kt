package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import org.springframework.stereotype.Component

@Component
class PriceService {

    fun downgrade(price: Double, downgradeFraction: Double, side: Side): Double {
        val aboveOne = price - 1
        val targetFairness = 1 - downgradeFraction
        check(targetFairness in 0.0..1.0)

        return when (side) {
            Side.LAY -> 1 + aboveOne * targetFairness
            Side.BACK -> 1 + aboveOne / targetFairness
            else -> throw IllegalStateException()
        }
    }

    fun isDowngrade(newPrice: Double, oldPrice: Double, type: Side): Boolean {
        if (newPrice priceEq oldPrice) return false
        return if (type == Side.BACK) {
            newPrice > oldPrice
        } else {
            newPrice < oldPrice
        }
    }

    /**
     * https://cs.wikipedia.org/wiki/S%C3%A1zkov%C3%BD_kurz
     */
    fun getFairnessFairPrice(unfairPrice: Double, fairness: Double): Double {
        return 1 + (unfairPrice - 1) / fairness
    }

    /**
     * http://stats.stackexchange.com/questions/140269/how-to-convert-sport-odds-into-percentage
     */
    fun getOverroundFairPrice(unfairPrice: Double, overround: Double, runnerCount: Int): Double {
        val probability = 1 / unfairPrice - (overround - 1) / runnerCount
        require(probability > 0) { listOf(unfairPrice, overround, runnerCount) }
        return 1 / probability
    }


}
