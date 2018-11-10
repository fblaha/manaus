package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.BetAction
import cz.fb.manaus.core.repository.domain.Price
import cz.fb.manaus.core.repository.domain.RealizedBet
import org.springframework.stereotype.Component

@Component
class LastMatchedTradedVolumeCategorizer : RealizedBetCategorizer {

    private fun getCategory(tradedMean: Double, lastMatched: Double): String {
        return when {
            Price.priceEq(tradedMean, lastMatched) -> LAST_MATCHED + "_eqTradedMean"
            lastMatched > tradedMean -> LAST_MATCHED + "_gtTradedMean"
            else -> LAST_MATCHED + "_ltTradedMean"
        }
    }

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val action = realizedBet.betAction
        val tradedMean = action.getDoubleProperty(BetAction.TRADED_VOL_MEAN)
        return if (tradedMean.isPresent) {
            val marketPrices = realizedBet.betAction.marketPrices
            val lastMatchedPrice = marketPrices.getRunnerPrices(realizedBet.selectionId).lastMatchedPrice
            setOf(getCategory(tradedMean.asDouble, lastMatchedPrice!!))
        } else {
            emptySet()
        }
    }

    companion object {
        private const val LAST_MATCHED = "lastMatched"
    }
}
