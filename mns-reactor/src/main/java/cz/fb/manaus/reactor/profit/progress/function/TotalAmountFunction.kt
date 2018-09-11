package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class TotalAmountFunction : AbstractOfferedAmountFunction() {

    override fun getRunnerPrices(bet: SettledBet): RunnerPrices {
        val marketPrices = bet.betAction.marketPrices
        return marketPrices.getRunnerPrices(bet.selectionId)
    }

}
