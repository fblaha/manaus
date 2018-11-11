package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.getReciprocal
import org.springframework.stereotype.Component

@Component
class ReciprocalLayFunction : ProgressFunction {
    override fun invoke(bet: RealizedBet): Double? {
        return getReciprocal(bet.betAction.runnerPrices, Side.LAY)
    }
}
