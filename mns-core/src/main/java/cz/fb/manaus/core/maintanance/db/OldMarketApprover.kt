package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.conf.DatabaseConf
import cz.fb.manaus.core.model.MarketFootprint
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class OldMarketApprover(private val databaseConf: DatabaseConf) : MarketDeletionApprover {

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val oldBoundary = Instant.now().minus(databaseConf.marketHistoryDays, ChronoUnit.DAYS)
        return oldBoundary.isAfter(marketFootprint.market.openDate)
    }

    override val timeRange: TimeRange?
        get() = TimeRange(null, Instant.now().minus(databaseConf.marketHistoryDays, ChronoUnit.DAYS))
}