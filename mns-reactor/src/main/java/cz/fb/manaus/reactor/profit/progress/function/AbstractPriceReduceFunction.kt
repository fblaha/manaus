package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import java.util.*

abstract class AbstractPriceReduceFunction protected constructor(
        private val side: Side,
        private val operator: (Double, Double) -> Double) : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val marketPrices = bet.betAction.marketPrices.getHomogeneous(side)
        val bestPrices = marketPrices.getBestPrices(side)
        return if (bestPrices.all { it.isPresent }) {
            val result = bestPrices
                    .filter { it.isPresent }
                    .map { it.asDouble }
                    .reduce(operator)
            OptionalDouble.of(result)
        } else {
            OptionalDouble.empty()
        }
    }
}
