package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ValidationMetricsCollectorTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var metricsCollector: ValidationMetricsCollector
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    @Test
    fun `validation metrics`() {
        val validator = Validator { _ -> ValidationResult.ACCEPT }
        metricsCollector.updateMetrics(ValidationResult.ACCEPT, Side.BACK, validator.name)
        metricsCollector.updateMetrics(ValidationResult.REJECT, Side.BACK, validator.name)
        val keys = metricRegistry.counters.keys
                .filter { key -> key.startsWith(ValidationMetricsCollector.PREFIX) }
                .filter { key -> key.contains(validator.name) }

        assertEquals(2, keys.size)
        keys.forEach { key -> assertEquals(1L, metricRegistry.counter(key).count) }
    }

}