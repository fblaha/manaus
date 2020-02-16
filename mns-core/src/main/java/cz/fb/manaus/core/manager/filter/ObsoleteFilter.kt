package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketSnapshotEvent
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Logger

@Component
class ObsoleteFilter : MarketSnapshotEventFilter {

    private val log = Logger.getLogger(ObsoleteFilter::class.simpleName)

    override fun accept(event: MarketSnapshotEvent): Boolean {
        return accept(event.snapshot.market)
    }

    private fun accept(market: Market): Boolean {
        val result = market.event.openDate.isAfter(Instant.now())
        if (!result) {
            log.finest { "omitting obsolete date '${market.event.openDate}' for '$market'" }
        }
        return result
    }

}