package cz.fb.manaus.core.manager

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.manager.filter.MarketSnapshotEventFilter
import cz.fb.manaus.core.model.MarketSnapshotEvent
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component


@Component
class MarketSnapshotEventFilterService(private val marketEventFilters: List<MarketSnapshotEventFilter>) {

    fun accept(event: MarketSnapshotEvent): Boolean {
        val filters = marketEventFilters.filter(event.account.provider::matches)
        return filters.all { accept(it, event) }
    }

    fun accept(filter: MarketSnapshotEventFilter, event: MarketSnapshotEvent): Boolean {
        val result = filter.accept(event)
        Metrics.counter(
                "mns_market_validator_stats",
                "validator", makeName(filter),
                "result", result.toString().toLowerCase()
        ).increment()
        return result
    }

}
