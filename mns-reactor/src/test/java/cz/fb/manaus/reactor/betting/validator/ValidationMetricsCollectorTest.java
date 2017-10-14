package cz.fb.manaus.reactor.betting.validator;

import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ValidationMetricsCollectorTest extends AbstractLocalTestCase {

    public static final Validator VALIDATOR = context -> ValidationResult.ACCEPT;
    @Autowired
    private ValidationMetricsCollector metricsCollector;

    @Test
    public void testMetrics() throws Exception {
        metricsCollector.updateMetrics(ValidationResult.ACCEPT, Side.BACK, VALIDATOR.getName());
        metricsCollector.updateMetrics(ValidationResult.REJECT, Side.BACK, VALIDATOR.getName());
        List<MetricRecord<?>> records = metricsCollector.getMetricRecords().collect(Collectors.toList());
        assertEquals(2, records.size());
        records.forEach(record -> assertEquals(1L, record.getValue()));
    }
}