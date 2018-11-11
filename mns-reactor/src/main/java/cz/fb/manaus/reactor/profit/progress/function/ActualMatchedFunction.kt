package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class ActualMatchedFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double {
        val prices = bet.betAction.runnerPrices
        return prices
                .filter { p -> p.matchedAmount != null }
                .mapNotNull { it.matchedAmount }
                .sum()
    }

}
