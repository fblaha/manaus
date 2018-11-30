package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.conf.MarketConf
import cz.fb.manaus.core.model.MarketFootprint
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OldMarketApprover(private val marketConf: MarketConf) : MarketDeletionApprover {

    override fun isDeletable(marketFootprint: MarketFootprint): Boolean {
        val oldBoundary = Instant.now().minus(marketConf.history)
        return oldBoundary.isAfter(marketFootprint.market.openDate)
    }

    override val timeRange: TimeRange?
        get() = TimeRange(null, Instant.now().minus(marketConf.history))
}