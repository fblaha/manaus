package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetEvent
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
        val validator: Validator = object : Validator {
            override fun validate(event: BetEvent): ValidationResult {
                return ValidationResult.ACCEPT
            }
        }
        metricsCollector.updateMetrics(ValidationResult.ACCEPT, Side.BACK, validator.name)
        metricsCollector.updateMetrics(ValidationResult.REJECT, Side.BACK, validator.name)
        val keys = metricRegistry.counters.keys
                .filter { it.startsWith(ValidationMetricsCollector.PREFIX) }
                .filter { validator.name in it }

        assertEquals(2, keys.size)
        keys.forEach { assertEquals(1L, metricRegistry.counter(it).count) }
    }

}