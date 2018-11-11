package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side

abstract class AbstractPriceReduceFunction(
        private val side: Side,
        private val operator: (Double, Double) -> Double) : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val bestPrices = bet.betAction.runnerPrices.map { it.getHomogeneous(side).bestPrice }
        return if (bestPrices.all { it != null }) {
            bestPrices.filterNotNull().map { it.price }.reduce(operator)
        } else {
            null
        }
    }
}
