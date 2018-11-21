package cz.fb.manaus.reactor.price

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.stereotype.Component

@Component
class PriceService(private val roundingService: RoundingService) {

    fun downgrade(price: Double, downgradeFraction: Double, side: Side): Double {
        val aboveOne = price - 1
        val targetFairness = 1 - downgradeFraction
        Preconditions.checkState(targetFairness in 0.0..1.0)

        if (side === Side.LAY) {
            return 1 + aboveOne * targetFairness
        } else if (side === Side.BACK) {
            return 1 + aboveOne / targetFairness
        }
        throw IllegalStateException()
    }

    fun isDowngrade(newPrice: Double, oldPrice: Double, type: Side): Boolean {
        if (Price.priceEq(newPrice, oldPrice)) return false
        return if (type === Side.BACK) {
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
        Preconditions.checkArgument(probability > 0, listOf(unfairPrice, overround, runnerCount))
        return 1 / probability
    }

    fun getRoundedFairnessFairPrice(unfairPrice: Double, fairness: Double): Double? {
        return roundingService.roundBet(getFairnessFairPrice(unfairPrice, fairness))
    }

}
