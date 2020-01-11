package cz.fb.manaus.core.manager

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.manager.filter.MarketFilter
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.provider.ProviderMatcher
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component


@Component
class MarketFilterService(private val marketFilters: List<MarketFilter>) {

    fun accept(market: Market, hasBets: Boolean, providerMatcher: ProviderMatcher): Boolean {
        var filters = marketFilters.filter(providerMatcher)
        if (hasBets) {
            filters = filters.filter { it.isStrict }
        }
        return filters.all { accept(it, market) }
    }

    fun accept(filter: MarketFilter, market: Market): Boolean {
        val result = filter.accept(market)
        Metrics.counter("mns_market_filter_stats",
                "filter", makeName(filter),
                "result", result.toString().toLowerCase()
        ).increment()
        return result
    }

}
