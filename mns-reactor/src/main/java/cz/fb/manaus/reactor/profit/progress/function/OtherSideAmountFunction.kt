package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.RunnerPrices
import org.springframework.stereotype.Component

@Component
class OtherSideAmountFunction : AbstractOfferedAmountFunction() {

    override fun getRunnerPrices(bet: RealizedBet): RunnerPrices {
        val marketPrices = bet.betAction.runnerPrices
        val prices = cz.fb.manaus.reactor.price.getRunnerPrices(marketPrices, bet.settledBet.selectionId)
        val side = bet.settledBet.price.side.opposite
        return prices.getHomogeneous(side)
    }

}
