package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class MatchedAmountDiffFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double {
        val actionAmount = bet.betAction.price.amount
        val betAmount = bet.settledBet.price.amount
        return actionAmount - betAmount
    }

}
