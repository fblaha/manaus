package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class SelectionActualMatchedCategorizer : AbstractMatchedCategorizer("actualMatchedSelection_") {

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    public override fun getAmount(bet: SettledBet): OptionalDouble {
        val matchedAmount = bet.betAction
                .marketPrices
                .getRunnerPrices(bet.selectionId)
                .matchedAmount
        return if (matchedAmount == null) OptionalDouble.empty() else OptionalDouble.of(matchedAmount)
    }

}
