package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class LastMatchedReciprocalCategorizer : SettledBetCategorizer {

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val marketPrices = settledBet.betAction.marketPrices
        val reciprocal = marketPrices.lastMatchedReciprocal
        return if (reciprocal.isPresent) {
            when {
                Price.priceEq(reciprocal.asDouble, 1.0) -> setOf(PREFIX + "eq1")
                reciprocal.asDouble > 1.0 -> setOf(PREFIX + "above1")
                else -> setOf(PREFIX + "bellow1")
            }
        } else emptySet()
    }

    companion object {
        const val PREFIX = "lastMatchedReciprocal_"
    }
}
