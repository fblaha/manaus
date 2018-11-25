package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Joiner
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class ValidationMetricsCollector(private val metricRegistry: MetricRegistry) {

    fun updateMetrics(result: ValidationResult, type: Side, validatorName: String) {
        val name = getName(type, result.isSuccess, validatorName)
        metricRegistry.counter(name).inc()
    }

    private fun getName(type: Side, pass: Boolean, validatorName: String): String {
        return Joiner.on('.').join(PREFIX,
                type.name.toLowerCase(), validatorName,
                if (pass) "pass" else "fail")
    }

    companion object {
        const val PREFIX = "validator.stats"
    }
}
