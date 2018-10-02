package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class LastMatchedTradedVolumeCategorizer : SettledBetCategorizer {

    private fun getCategory(tradedMean: Double, lastMatched: Double): String {
        return when {
            Price.priceEq(tradedMean, lastMatched) -> LAST_MATCHED + "_eqTradedMean"
            lastMatched > tradedMean -> LAST_MATCHED + "_gtTradedMean"
            else -> LAST_MATCHED + "_ltTradedMean"
        }
    }

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val action = settledBet.betAction
        val tradedMean = action.getDoubleProperty(BetAction.TRADED_VOL_MEAN)
        return if (tradedMean.isPresent) {
            val marketPrices = settledBet.betAction.marketPrices
            val lastMatchedPrice = marketPrices.getRunnerPrices(settledBet.selectionId).lastMatchedPrice
            setOf(getCategory(tradedMean.asDouble, lastMatchedPrice!!))
        } else {
            emptySet()
        }
    }

    companion object {
        private const val LAST_MATCHED = "lastMatched"
    }
}
