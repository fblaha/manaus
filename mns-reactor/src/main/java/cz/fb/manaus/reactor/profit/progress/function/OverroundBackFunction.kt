package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.getOverround
import org.springframework.stereotype.Component

@Component
object OverroundBackFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        return getOverround(bet.betAction.runnerPrices, Side.BACK)
    }

}
