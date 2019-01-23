package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.getReciprocal
import org.springframework.stereotype.Component

@Component
object ReciprocalBackFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        return getReciprocal(bet.betAction.runnerPrices, Side.BACK)
    }

}
