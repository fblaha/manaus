package cz.fb.manaus.reactor.profit.metrics

import com.google.common.util.concurrent.AtomicDouble
import cz.fb.manaus.reactor.profit.ProfitLoader
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger


@Component
@Profile(ManausProfiles.DB)
class ProfitMetricUpdater(
        private val profitLoader: ProfitLoader,
        specs: List<ProfitMetricSpec> = emptyList()
) {

    private val log = Logger.getLogger(ProfitMetricUpdater::class.simpleName)

    private fun makeMetrics(spec: ProfitMetricSpec): Map<String, AtomicDouble> =
            spec.categoryValues
                    .map { it to Metrics.gauge(spec.metricName, listOf(Tag.of(spec.categoryPrefix, it)), AtomicDouble()) }
                    .toMap()

    private val allMetrics: Map<String, Map<String, AtomicDouble>> = specs
            .map { it.metricName to makeMetrics(it) }
            .toMap()

    private val byInterval = specs.groupBy { it.interval }

    @Scheduled(fixedRateString = "PT15M")
    fun computeMetricsHighFreq() {
        computeMetrics(UpdateFrequency.HIGH)
    }

    @Scheduled(fixedRateString = "PT60M")
    fun computeMetricsMediumFreq() {
        computeMetrics(UpdateFrequency.MEDIUM)
    }

    @Scheduled(fixedRateString = "PT240M")
    fun computeMetricsLowFreq() {
        computeMetrics(UpdateFrequency.LOW)
    }

    private fun computeMetrics(updateFrequency: UpdateFrequency) {
        log.info { "updating profit metric for frequency $updateFrequency" }
        for ((interval, specs) in byInterval.entries) {
            if (specs.any { it.updateFrequency == updateFrequency }) {
                val records = profitLoader.loadProfitRecords(interval, true)
                for (spec in specs) {
                    val relevantRecords = records.filter(spec.recordPredicate)
                    val metrics = allMetrics[spec.metricName] ?: error("missing metric")
                    for (record in relevantRecords) {
                        val categoryVal = spec.extractVal(record.category)
                        log.info { "updating profit metric ${spec.metricName} - value: $categoryVal, profit: ${record.profit}" }
                        metrics[categoryVal]?.set(record.profit)
                    }
                }
            }
        }
    }
}