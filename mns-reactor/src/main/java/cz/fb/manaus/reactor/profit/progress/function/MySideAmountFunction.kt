package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class MySideAmountFunction : AbstractOfferedAmountFunction() {

    override fun getRunnerPrices(bet: SettledBet): RunnerPrices {
        val marketPrices = bet.betAction.marketPrices
        val prices = marketPrices.getRunnerPrices(bet.selectionId)
        val side = bet.price.side
        return prices.getHomogeneous(side)
    }

}
