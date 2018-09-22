package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class ActualMatchedFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        val prices = bet.betAction.marketPrices.runnerPrices
        return prices
                .filter { p -> p.matchedAmount != null }
                .map { it.matchedAmount }
                .sum()
    }

}
