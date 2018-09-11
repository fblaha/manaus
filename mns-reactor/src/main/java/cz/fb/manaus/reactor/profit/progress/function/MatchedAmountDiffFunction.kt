package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class MatchedAmountDiffFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val actionAmount = bet.betAction.price.amount
        val betAmount = bet.price.amount
        return OptionalDouble.of(actionAmount - betAmount)
    }

}
