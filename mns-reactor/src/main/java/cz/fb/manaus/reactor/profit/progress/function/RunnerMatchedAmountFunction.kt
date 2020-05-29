package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object RunnerMatchedAmountFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val prices = bet.betAction.runnerPrices.first { it.selectionId == bet.betAction.selectionId }
        return prices.matchedAmount
    }

}
