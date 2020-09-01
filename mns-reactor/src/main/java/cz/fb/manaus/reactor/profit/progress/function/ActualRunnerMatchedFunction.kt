package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object ActualRunnerMatchedFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        return bet.betAction.runnerPrices
            .find { it.selectionId == bet.settledBet.selectionId }?.matchedAmount
    }

}
