package cz.fb.manaus.core.manager

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.manager.filter.MarketSnapshotEventValidator
import cz.fb.manaus.core.model.MarketSnapshotEvent
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component


@Component
class MarketSnapshotEventValidationService(private val marketEventValidators: List<MarketSnapshotEventValidator>) {

    fun accept(event: MarketSnapshotEvent): Boolean {
        val filters = marketEventValidators.filter(event.account.provider::matches)
        return filters.all { accept(it, event) }
    }

    fun accept(validator: MarketSnapshotEventValidator, event: MarketSnapshotEvent): Boolean {
        val result = validator.accept(event)
        Metrics.counter("mns_market_validator_stats",
                "validator", makeName(validator),
                "result", result.toString().toLowerCase()
        ).increment()
        return result
    }

}
