package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.reactor.price.getRunnerPrices
import org.springframework.stereotype.Component

@Component
object MySideAmountFunction : AbstractOfferedAmountFunction() {

    override fun getRunnerPrices(bet: RealizedBet): RunnerPrices {
        val marketPrices = bet.betAction.runnerPrices
        val prices = getRunnerPrices(marketPrices, bet.settledBet.selectionId)
        val side = bet.settledBet.price.side
        return prices.by(side)
    }

}
