package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side

abstract class AbstractPriceReduceFunction(
        private val side: Side,
        private val operator: (Double, Double) -> Double) : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val marketPrices = bet.betAction.marketPrices.getHomogeneous(side)
        val bestPrices = marketPrices.getBestPrices(side)
        return if (bestPrices.all { it.isPresent }) {
            bestPrices
                    .filter { it.isPresent }
                    .map { it.asDouble }
                    .reduce(operator)

        } else {
            null
        }
    }
}
