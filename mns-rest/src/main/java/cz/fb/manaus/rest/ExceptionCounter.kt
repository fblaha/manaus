package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionCounter(private val metricRegistry: MetricRegistry) {

    @ExceptionHandler(value = [Throwable::class])
    @Throws(Throwable::class)
    fun defaultErrorHandler(e: Throwable) {
        metricRegistry.counter("_ERROR_").inc()
        throw e
    }

}