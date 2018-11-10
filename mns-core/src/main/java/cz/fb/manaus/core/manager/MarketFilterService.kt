package cz.fb.manaus.core.manager

import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Service


@Service
class MarketFilterService(private val marketFilters: List<MarketFilter> = mutableListOf()) {

    fun accept(market: Market, hasBets: Boolean, categoryBlacklist: Set<String>): Boolean {
        var filters = marketFilters
        if (hasBets) {
            filters = filters.filter { it.isStrict }
        }
        return filters.all { it.accept(market, categoryBlacklist) }
    }
}
