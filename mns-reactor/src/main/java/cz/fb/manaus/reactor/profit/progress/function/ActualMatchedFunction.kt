package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class ActualMatchedFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val prices = bet.betAction.marketPrices.runnerPrices
        val sum = prices
                .filter { p -> p.matchedAmount != null }
                .map { it.matchedAmount }
                .sum()
        return OptionalDouble.of(sum)
    }

}
