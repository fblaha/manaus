package cz.fb.manaus.reactor.profit.metrics

import com.google.common.util.concurrent.AtomicDouble
import cz.fb.manaus.reactor.profit.ProfitLoader
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

// TODO unit test

@Component
@Profile(ManausProfiles.DB)
class ProfitMetricManager(
        private val profitLoader: ProfitLoader,
        private val descriptors: List<ProfitMetricDescriptor> = emptyList()
) {

    private fun makeMetrics(descriptor: ProfitMetricDescriptor): Map<String, AtomicDouble> =
            descriptor.categoryValues
                    .map { it to Metrics.gauge(descriptor.metricName, listOf(Tag.of(descriptor.categoryPrefix, it)), AtomicDouble()) }
                    .toMap()

    private val allMetrics: Map<String, Map<String, AtomicDouble>> = descriptors
            .map { it.metricName to makeMetrics(it) }
            .toMap()

    @Scheduled(fixedRateString = "PT30M")
    fun computeMetrics() {
        for (spec in descriptors) {
            val records = profitLoader.loadProfitRecords(spec.interval, true, spec.projection)
            val relevantRecords = records.filter { it.category.startsWith(spec.categoryPrefix) }
            val metrics = allMetrics[spec.metricName] ?: error("missing metric")
            for (r in relevantRecords) {
                val categoryVal = spec.extractVal(r.category)
                metrics[categoryVal]?.set(r.profit)
            }
        }
    }

}