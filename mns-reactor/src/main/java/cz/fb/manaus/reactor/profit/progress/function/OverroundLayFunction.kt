package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class OverroundLayFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val overround = bet.betAction.marketPrices.getOverround(Side.LAY)
        return if (overround.isPresent) overround.asDouble else null
    }

}
