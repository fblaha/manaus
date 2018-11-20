package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.model.MarketFootprint
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class OldMarketApprover(
        @param:Value(MarketCleaner.HIST_DAYS_EL) private val marketHistoryDays: Long) : MarketDeletionApprover {

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val oldBoundary = Instant.now().minus(marketHistoryDays, ChronoUnit.DAYS)
        return oldBoundary.isAfter(marketFootprint.market.openDate)
    }

    override val timeRange: TimeRange?
        get() = TimeRange(null, Instant.now().minus(marketHistoryDays, ChronoUnit.DAYS))
}