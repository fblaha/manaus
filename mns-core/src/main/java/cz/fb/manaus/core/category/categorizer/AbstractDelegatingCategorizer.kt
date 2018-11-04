package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.SettledBet


abstract class AbstractDelegatingCategorizer(private val prefix: String) : SettledBetCategorizer, Categorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        return getCategories(settledBet.betAction.market)
    }

    override fun getCategories(market: Market): Set<String> {
        return getPrefixedCategories(market)
    }

    protected abstract fun getCategoryRaw(market: Market): Set<String>?

    private fun getPrefixedCategories(market: Market): Set<String> {
        val categories = getCategoryRaw(market) ?: return emptySet()
        return categories.map { input -> Category.MARKET_PREFIX + prefix + input }.toSet()
    }
}