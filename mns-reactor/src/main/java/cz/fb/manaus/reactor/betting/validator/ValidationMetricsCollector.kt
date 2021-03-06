package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Side
import io.micrometer.core.instrument.Metrics
import org.springframework.stereotype.Component

@Component
object ValidationMetricsCollector {

    fun updateMetrics(result: ValidationResult, type: Side, validatorName: String) {
        Metrics.counter(
            "mns_validator_stats",
            "side", type.name.lowercase(),
            "validator", validatorName,
            "result", result.name.lowercase()
        ).increment()
    }
}
