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
import kotlin.time.ExperimentalTime


@Component
@ExperimentalTime
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

    private val byQuery = specs.groupBy { it.query }

    @Scheduled(fixedRateString = "PT15M")
    fun computeMetricsHighFreq() {
        computeMetrics(UpdateFrequency.HIGH)
    }

    @Scheduled(fixedRateString = "PT1H")
    fun computeMetricsMediumFreq() {
        computeMetrics(UpdateFrequency.MEDIUM)
    }

    @Scheduled(fixedRateString = "PT4H")
    fun computeMetricsLowFreq() {
        computeMetrics(UpdateFrequency.LOW)
    }

    private fun computeMetrics(updateFrequency: UpdateFrequency) {
        log.info { "updating profit metric for frequency: $updateFrequency" }
        for ((query, specs) in byQuery.entries) {
            if (query.updateFrequency == updateFrequency) {
                val records = profitLoader.loadProfitRecords(query.interval, true, query.projection)
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