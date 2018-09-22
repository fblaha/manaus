package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class SelectionActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedSelection_") {

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    override fun getAmount(settledBet: SettledBet): Double? {
        return settledBet.betAction
                .marketPrices
                .getRunnerPrices(settledBet.selectionId)
                .matchedAmount
    }

}
