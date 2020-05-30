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

// TODO unit test

@Component
@Profile(ManausProfiles.DB)
class ProfitMetricManager(
        private val profitLoader: ProfitLoader,
        private val descriptors: List<ProfitMetricDescriptor> = emptyList()
) {

    private val log = Logger.getLogger(ProfitMetricManager::class.simpleName)

    private fun makeMetrics(descriptor: ProfitMetricDescriptor): Map<String, AtomicDouble> =
            descriptor.categoryValues
                    .map { it to Metrics.gauge(descriptor.metricName, listOf(Tag.of(descriptor.categoryPrefix, it)), AtomicDouble()) }
                    .toMap()

    private val allMetrics: Map<String, Map<String, AtomicDouble>> = descriptors
            .map { it.metricName to makeMetrics(it) }
            .toMap()

    @Scheduled(fixedRateString = "PT30M")
    fun computeMetrics() {
        for ((interval, descriptors) in descriptors.groupBy { it.interval }.entries) {
            val records = profitLoader.loadProfitRecords(interval, true)
            for (descriptor in descriptors) {
                val relevantRecords = records.filter { it.category.startsWith(descriptor.categoryPrefix) }
                val metrics = allMetrics[descriptor.metricName] ?: error("missing metric")
                for (r in relevantRecords) {
                    val categoryVal = descriptor.extractVal(r.category)
                    log.info { "updating profit metric ${descriptor.metricName} - value: $categoryVal, profit: ${r.profit}" }
                    metrics[categoryVal]?.set(r.profit)
                }
            }
        }
    }

}