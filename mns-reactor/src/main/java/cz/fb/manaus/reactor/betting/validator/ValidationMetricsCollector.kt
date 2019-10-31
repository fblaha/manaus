package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class ValidationMetricsCollector(private val metricRegistry: MetricRegistry) {

    fun updateMetrics(result: ValidationResult, type: Side, validatorName: String) {
        val name = getName(type, result, validatorName)
        metricRegistry.counter(name).inc()
    }

    private fun getName(type: Side, result: ValidationResult, validatorName: String): String {
        return "$PREFIX.${type.name.toLowerCase()}.$validatorName.${result.name.toLowerCase()}"
    }

    companion object {
        const val PREFIX = "validator.stats"
    }
}
