package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.getRunnerPrices
import org.springframework.stereotype.Component

@Component
class MySideAmountFunction : AbstractOfferedAmountFunction() {

    override fun getRunnerPrices(bet: RealizedBet): RunnerPrices {
        val marketPrices = bet.betAction.runnerPrices
        val prices = getRunnerPrices(marketPrices, bet.settledBet.selectionId)
        val side = bet.settledBet.price.side
        return prices.getHomogeneous(side)
    }

}
