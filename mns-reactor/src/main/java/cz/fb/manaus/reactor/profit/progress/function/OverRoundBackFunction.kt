package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.getOverRound
import org.springframework.stereotype.Component

@Component
object OverRoundBackFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        return getOverRound(bet.betAction.runnerPrices, Side.BACK)
    }

}
