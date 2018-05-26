package cz.fb.manaus.reactor.betting.validator;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ValidationMetricsCollectorTest extends AbstractLocalTestCase {

    public static final Validator VALIDATOR = context -> ValidationResult.ACCEPT;
    @Autowired
    private ValidationMetricsCollector metricsCollector;
    @Autowired
    private MetricRegistry metricRegistry;

    @Test
    public void testMetrics() {
        metricsCollector.updateMetrics(ValidationResult.ACCEPT, Side.BACK, VALIDATOR.getName());
        metricsCollector.updateMetrics(ValidationResult.REJECT, Side.BACK, VALIDATOR.getName());
        var keys = metricRegistry.getCounters().keySet().stream()
                .filter(key -> key.startsWith(ValidationMetricsCollector.PREFIX))
                .filter(key -> key.contains(VALIDATOR.getName()))
                .collect(Collectors.toList());

        assertEquals(2, keys.size());
        keys.forEach(key -> assertEquals(1L, metricRegistry.counter(key).getCount()));
    }
}