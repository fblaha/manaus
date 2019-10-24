package cz.fb.manaus.core.manager

import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.provider.ProviderMatcher
import org.springframework.stereotype.Component


@Component
class MarketFilterService(private val marketFilters: List<MarketFilter>) {

    fun accept(market: Market, hasBets: Boolean, providerMatcher: ProviderMatcher): Boolean {
        var filters = marketFilters.filter(providerMatcher)
        if (hasBets) {
            filters = filters.filter { it.isStrict }
        }
        return filters.all { it.accept(market) }
    }
}
