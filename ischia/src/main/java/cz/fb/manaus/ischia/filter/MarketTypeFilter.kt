package cz.fb.manaus.ischia.filter

import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.model.Market

class MarketTypeFilter(private val allowedTypes: Set<String>) : MarketFilter {

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val type = market.type?.toLowerCase()
        return type in allowedTypes
    }
}
