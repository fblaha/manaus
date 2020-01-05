package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Side
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component

@Component
class ValidationMetricsCollector {

    fun updateMetrics(result: ValidationResult, type: Side, validatorName: String) {
        Metrics.counter("validator.stats",
                "side", type.name.toLowerCase(),
                "validator", validatorName,
                "result", result.name.toLowerCase()
        )
    }
}
