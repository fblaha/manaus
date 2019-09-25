package cz.fb.manaus.rest

import com.codahale.metrics.Histogram
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.SlidingWindowReservoir
import cz.fb.manaus.core.model.Bet
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class MatchedBetMetricUpdater(private val metricRegistry: MetricRegistry) {
    private val metricName = "bet.matched.count"
    private val lastScan = AtomicLong(0)

    private fun newHistogram(): Histogram {
        return Histogram(SlidingWindowReservoir(100))
    }

    fun update(scanTime: Long, bets: List<Bet>) {
        val last = lastScan.getAndSet(scanTime)
        if (last > 0 && last != scanTime) {
            val lastCount = metricRegistry.counter(metricName).count
            metricRegistry.remove(metricName)
            metricRegistry.histogram("bet.matched") { this.newHistogram() }.update(lastCount)
        }
        val currentCount = bets.filter { it.isHalfMatched }.count()
        if (currentCount > 0) {
            metricRegistry.counter(metricName).inc(currentCount.toLong())
        }
    }
}
