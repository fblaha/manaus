package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Market
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class LookAheadFilter(private val lookAhead: Duration) : MarketFilter {

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val start = market.openDate
        val untilStart = Instant.now().until(start, ChronoUnit.MINUTES)
        return untilStart < lookAhead.toMinutes()
    }
}
