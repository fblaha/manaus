package cz.fb.manaus.core.metrics

import com.codahale.metrics.Counter
import com.codahale.metrics.Histogram
import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MetricsService {

    @Autowired
    private lateinit var registry: MetricRegistry

    fun getCollectedMetrics(prefix: String): List<MetricRecord<*>> {
        val counters = registry.counters.entries
                .flatMap { (k, v) -> getCounterMetricRecords(k, v) }
        val meters = registry.meters.entries
                .flatMap { (k, v) -> getMeterMetricRecords(k, v) }
        val histograms = registry.histograms.entries
                .flatMap { (k, v) -> getHistogramMetricRecords(k, v) }

        return (counters + meters + histograms)
                .filter { it.name.startsWith(prefix) }
                .sortedBy { it.name }

    }

    private fun getMeterMetricRecords(name: String, meter: Meter): List<MetricRecord<*>> {
        return listOf(
                MetricRecord("$name.count", meter.count),
                MetricRecord("$name.rate15", meter.fifteenMinuteRate),
                MetricRecord("$name.rate5", meter.fiveMinuteRate),
                MetricRecord("$name.rate1", meter.oneMinuteRate))
    }

    private fun getCounterMetricRecords(name: String, counter: Counter): List<MetricRecord<*>> {
        return listOf(MetricRecord(name, counter.count))
    }

    private fun getHistogramMetricRecords(name: String, histogram: Histogram): List<MetricRecord<*>> {
        val snapshot = histogram.snapshot
        return listOf(
                MetricRecord("$name.max", snapshot.max),
                MetricRecord("$name.min", snapshot.min))
    }
}
