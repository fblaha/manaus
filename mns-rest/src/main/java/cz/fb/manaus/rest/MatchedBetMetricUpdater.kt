package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Bet
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class MatchedBetMetricUpdater {
    private val lastScan = AtomicLong(0)
    private val matchedBets: AtomicLong by lazy { Metrics.gauge("bet_matched_count", AtomicLong()) }


    fun update(scanTime: Long, bets: List<Bet>) {
        val last = lastScan.getAndSet(scanTime)
        if (last > 0 && last != scanTime) {
            matchedBets.set(0)
        }
        val currentCount = bets.filter { it.isHalfMatched }.count()
        if (currentCount > 0) {
            matchedBets.addAndGet(currentCount.toLong())
        }
    }
}
