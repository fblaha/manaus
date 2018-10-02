package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class ActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedMarket_") {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getAmount(settledBet: SettledBet): Double? {
        val runnerPrices = settledBet.betAction
                .marketPrices.runnerPrices.filter { p -> p.matchedAmount != null }
        return if (runnerPrices.isEmpty()) {
            null
        } else {
            runnerPrices.map { it.matchedAmount }.sum()
        }
    }

}
