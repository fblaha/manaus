package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.getRunnerPrices
import org.springframework.stereotype.Component

@Component
class SelectionActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedSelection_") {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getAmount(realizedBet: RealizedBet): Double? {
        return getRunnerPrices(realizedBet.betAction.runnerPrices, realizedBet.settledBet.selectionId).matchedAmount

    }

}
