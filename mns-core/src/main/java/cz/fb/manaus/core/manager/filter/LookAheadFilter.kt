package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.conf.MarketConf
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class LookAheadFilter(private val marketConf: MarketConf) : MarketFilter {

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val start = market.openDate
        val untilStart = Instant.now().until(start, ChronoUnit.MINUTES)
        return untilStart < marketConf.lookAhead.toMinutes()
    }
}
