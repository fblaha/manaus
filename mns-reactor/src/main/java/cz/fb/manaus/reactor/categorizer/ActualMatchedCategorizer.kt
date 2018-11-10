package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.stereotype.Component

@Component
class ActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedMarket_") {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getAmount(realizedBet: RealizedBet): Double? {
        return realizedBet.betAction.runnerPrices.mapNotNull { it.matchedAmount }.sum()
    }

}
