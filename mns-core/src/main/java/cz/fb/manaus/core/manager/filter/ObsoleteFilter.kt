package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

@Component
class ObsoleteFilter : MarketFilter {

    private val log = Logger.getLogger(ObsoleteFilter::class.java.simpleName)

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val result = market.event.openDate.isAfter(Instant.now())
        if (!result) {
            log.log(Level.FINEST, "Omitting obsolete date ''{0}'' for ''{1}''", arrayOf(market.event.openDate, market))
        }
        return result
    }

    override val isStrict: Boolean = true
}