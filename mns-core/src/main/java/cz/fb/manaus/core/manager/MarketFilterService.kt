package cz.fb.manaus.core.manager

import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.provider.ProviderCapability
import org.springframework.stereotype.Component


@Component
class MarketFilterService(private val marketFilters: List<MarketFilter>) {

    fun accept(market: Market, hasBets: Boolean, providerCapabilities: Set<ProviderCapability>): Boolean {
        var filters = marketFilters.filter { it.allIn(providerCapabilities) }
        if (hasBets) {
            filters = filters.filter { it.isStrict }
        }
        return filters.all { it.accept(market) }
    }
}
