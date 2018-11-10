package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.Price
import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.stereotype.Component

@Component
class LastMatchedReciprocalCategorizer : RealizedBetCategorizer {

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val marketPrices = realizedBet.betAction.marketPrices
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
