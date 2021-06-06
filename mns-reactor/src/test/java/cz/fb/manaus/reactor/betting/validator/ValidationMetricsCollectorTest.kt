package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import io.micrometer.core.instrument.Metrics
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestValidator : Validator {
    override fun validate(event: BetEvent): ValidationResult {
        return ValidationResult.OK
    }
}

class ValidationMetricsCollectorTest {

    private val collector = ValidationMetricsCollector

    @Test
    fun `validation metrics`() {
        val validator = TestValidator()
        collector.updateMetrics(ValidationResult.OK, Side.BACK, makeName(validator))
        collector.updateMetrics(ValidationResult.DROP, Side.BACK, makeName(validator))
        val meters = Metrics.globalRegistry.meters
            .filter { it.id.name.startsWith("mns_validator_stats") }
        assertTrue { meters.size >= 2 }
    }

}