package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class MatchedAmountDiffFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        val actionAmount = bet.betAction.price.amount
        val betAmount = bet.price.amount
        return actionAmount - betAmount
    }

}
