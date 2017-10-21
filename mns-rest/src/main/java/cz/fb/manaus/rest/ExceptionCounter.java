package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionCounter {

    public static final String METRIC_NAME = "_ERROR_";
    @Autowired
    private MetricRegistry metricRegistry;

    @ExceptionHandler(value = Throwable.class)
    public void defaultErrorHandler(Throwable e) throws Throwable {
        metricRegistry.counter(METRIC_NAME).inc();
        throw e;
    }
}