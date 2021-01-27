package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.MarketFootprint
import java.time.Duration
import java.time.Instant

class OldMarketApprover(private val history: Duration) : MarketDeletionApprover {

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val oldBoundary = Instant.now().minus(history)
        return oldBoundary.isAfter(marketFootprint.market.event.openDate)
    }

    override val timeRange: TimeRange
        get() = TimeRange(null, Instant.now().minus(history))
}