package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object ActualMarketMatchedFunction : ProgressFunction {

    override val includeNoValues: Boolean get() = false

    override fun invoke(bet: RealizedBet): Double? {
        val prices = bet.betAction.runnerPrices
        val amounts = prices.mapNotNull { it.matchedAmount }
        return when {
            amounts.isNotEmpty() -> amounts.sum()
            else -> null
        }
    }
}
