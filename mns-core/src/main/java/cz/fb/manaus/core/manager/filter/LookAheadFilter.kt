package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.repository.domain.Market
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class LookAheadFilter(@param:Value("#{systemEnvironment['MNS_LOOK_AHEAD'] ?: 7}") private val lookAheadDays: Int) : MarketFilter {

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val lookAhead = Duration.ofDays(lookAheadDays.toLong())
        val start = market.openDate
        val untilStart = Instant.now().until(start, ChronoUnit.MINUTES)
        return untilStart < lookAhead.toMinutes()
    }
}
