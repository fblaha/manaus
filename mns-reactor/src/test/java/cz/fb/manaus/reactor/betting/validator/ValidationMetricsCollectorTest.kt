package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.makeName
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class ValidationMetricsCollectorTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var metricsCollector: ValidationMetricsCollector
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    @Test
    fun `validation metrics`() {
        val validator: Validator = object : Validator {
            override fun validate(event: BetEvent): ValidationResult {
                return ValidationResult.OK
            }
        }
        metricsCollector.updateMetrics(ValidationResult.OK, Side.BACK, makeName(validator))
        metricsCollector.updateMetrics(ValidationResult.DROP, Side.BACK, makeName(validator))
        val keys = metricRegistry.counters.keys
                .filter { it.startsWith(ValidationMetricsCollector.PREFIX) }
                .filter { makeName(validator) in it }

        assertTrue { keys.size >= 2 }
        keys.forEach { assertTrue { metricRegistry.counter(it).count >= 1 } }
    }

}