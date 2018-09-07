package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionCounter {
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    @ExceptionHandler(value = [Throwable::class])
    @Throws(Throwable::class)
    fun defaultErrorHandler(e: Throwable) {
        metricRegistry.counter(METRIC_NAME).inc()
        throw e
    }

    companion object {
        const val METRIC_NAME = "_ERROR_"
    }
}