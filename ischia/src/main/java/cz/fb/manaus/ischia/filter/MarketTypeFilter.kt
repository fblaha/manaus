package cz.fb.manaus.ischia.filter

import cz.fb.manaus.core.manager.filter.FreshMarketValidator
import cz.fb.manaus.core.model.Market

class MarketTypeFilter(private val allowedTypes: Set<String>) : FreshMarketValidator {

    override fun accept(market: Market): Boolean {
        val type = market.type?.toLowerCase()
        return type in allowedTypes
    }
}
