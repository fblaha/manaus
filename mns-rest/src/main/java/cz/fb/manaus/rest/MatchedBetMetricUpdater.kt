package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Side
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class MatchedBetMetricUpdater {
    private val lastScan = AtomicLong(0)

    private val matchedLayBets: AtomicLong by lazy {
        Metrics.gauge("mns_bet_matched_count", listOf(Tag.of("side", "lay")), AtomicLong())
    }
    private val matchedBackBets: AtomicLong by lazy {
        Metrics.gauge("mns_bet_matched_count", listOf(Tag.of("side", "back")), AtomicLong())
    }

    fun update(scanTime: Long, bets: List<Bet>) {
        val last = lastScan.getAndSet(scanTime)
        if (last > 0 && last != scanTime) {
            matchedBackBets.set(0)
            matchedLayBets.set(0)
        }
        val matched = bets.filter { it.isHalfMatched }
        update(matched.count { it.requestedPrice.side == Side.BACK }, matchedBackBets)
        update(matched.count { it.requestedPrice.side == Side.LAY }, matchedLayBets)
    }

    private fun update(count: Int, counter: AtomicLong) {
        if (count > 0) {
            counter.addAndGet(count.toLong())
        }
    }
}
