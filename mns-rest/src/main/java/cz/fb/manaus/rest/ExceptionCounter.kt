package cz.fb.manaus.rest

import io.micrometer.core.instrument.Metrics
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionCounter {

    @ExceptionHandler(value = [Throwable::class])
    @Throws(Throwable::class)
    fun defaultErrorHandler(e: Throwable) {
        Metrics.counter("exception_count").increment()
        throw e
    }

}