package cz.fb.manaus.core.metrics;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MetricsServiceTest extends AbstractLocalTestCase {

    @Autowired
    private MetricsService service;
    @Autowired
    private MetricRegistry registry;

    @Test
    public void testCollectCounter() {
        registry.counter("test.counter").inc();
        var recordMap = getRecordMap();
        assertThat(recordMap.get("test.counter").getValue(), is(1L));
    }

    @Test
    public void testCollectMeter() {
        registry.meter("test.meter").mark(10);
        registry.meter("test.meter").mark(20);
        var recordMap = getRecordMap();
        assertThat(recordMap.get("test.meter.count").getValue(), is(30L));
    }

    @Test
    public void testCollectHistogram() {
        registry.histogram("test.hist").update(10);
        registry.histogram("test.hist").update(20);
        var recordMap = getRecordMap();
        assertThat(recordMap.get("test.hist.max").getValue(), is(20L));
        assertThat(recordMap.get("test.hist.min").getValue(), is(10L));
    }

    public Map<String, MetricRecord<?>> getRecordMap() {
        var collectedMetrics = service.getCollectedMetrics("test");
        return collectedMetrics.stream()
                .collect(Collectors.toMap(MetricRecord::getName, Function.identity()));
    }

}