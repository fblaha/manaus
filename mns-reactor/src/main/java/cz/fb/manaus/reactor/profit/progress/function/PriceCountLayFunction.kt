package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component

@Component
object PriceCountLayFunction : ProgressFunction {

    override val includeNoValues: Boolean get() = false

    override fun invoke(bet: RealizedBet): Double {
        val runnerPrices = getRunnerPrices(bet.betAction.runnerPrices, bet.settledBet.selectionId)
        return runnerPrices.by(Side.LAY).prices.size.toDouble()
    }
}
