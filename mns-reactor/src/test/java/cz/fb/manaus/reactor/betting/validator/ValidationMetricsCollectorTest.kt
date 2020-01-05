package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ValidationMetricsCollectorTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var metricsCollector: ValidationMetricsCollector

    // TODO micrometer
    @Test
    fun `validation metrics`() {
//        val validator: Validator = object : Validator {
//            override fun validate(event: BetEvent): ValidationResult {
//                return ValidationResult.OK
//            }
//        }
//        metricsCollector.updateMetrics(ValidationResult.OK, Side.BACK, makeName(validator))
//        metricsCollector.updateMetrics(ValidationResult.DROP, Side.BACK, makeName(validator))
//        val keys = metricRegistry.counters.keys
//                .filter { it.startsWith(ValidationMetricsCollector.PREFIX) }
//                .filter { makeName(validator) in it }
//
//        assertTrue { keys.size >= 2 }
//        keys.forEach { assertTrue { metricRegistry.counter(it).count >= 1 } }
    }

}