package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class ActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedMarket_") {

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    override fun getAmount(bet: SettledBet): Double? {
        val runnerPrices = bet.betAction
                .marketPrices.runnerPrices.filter { p -> p.matchedAmount != null }
        return if (runnerPrices.isEmpty()) {
            null
        } else {
            runnerPrices.map { it.matchedAmount }.sum()
        }
    }

}
