package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.repository.domain.Market
import cz.fb.manaus.core.repository.domain.RealizedBet


abstract class AbstractDelegatingCategorizer(private val prefix: String) : RealizedBetCategorizer, Categorizer {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        return getCategories(realizedBet.market)
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