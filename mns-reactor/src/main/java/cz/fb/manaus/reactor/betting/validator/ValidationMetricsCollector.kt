package cz.fb.manaus.reactor.betting.validator

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class ValidationMetricsCollector(private val metricRegistry: MetricRegistry) {

    fun updateMetrics(result: ValidationResult, type: Side, validatorName: String) {
        val name = getName(type, result.isSuccess, validatorName)
        metricRegistry.counter(name).inc()
    }

    private fun getName(type: Side, pass: Boolean, validatorName: String): String {
        val result = if (pass) "pass" else "fail"
        val strSide = type.name.toLowerCase()
        return "$PREFIX.$strSide.$validatorName.$result"
    }

    companion object {
        const val PREFIX = "validator.stats"
    }
}
