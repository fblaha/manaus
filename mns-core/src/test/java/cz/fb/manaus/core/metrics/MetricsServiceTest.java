package cz.fb.manaus.core.metrics;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
    public void testCollectCounter() throws Exception {
        registry.counter("test.counter").inc();
        Map<String, MetricRecord<?>> recordMap = getRecordMap();
        assertThat(recordMap.get("test.counter").getValue(), is(1L));
    }

    @Test
    public void testCollectMeter() throws Exception {
        registry.meter("test.meter").mark(10);
        registry.meter("test.meter").mark(20);
        Map<String, MetricRecord<?>> recordMap = getRecordMap();
        assertThat(recordMap.get("test.meter.count").getValue(), is(30L));
    }

    @Test
    public void testCollectHistogram() throws Exception {
        registry.histogram("test.hist").update(10);
        registry.histogram("test.hist").update(20);
        Map<String, MetricRecord<?>> recordMap = getRecordMap();
        assertThat(recordMap.get("test.hist.max").getValue(), is(20L));
        assertThat(recordMap.get("test.hist.min").getValue(), is(10L));
        assertThat(recordMap.get("test.hist.mean").getValue(), is(15.0d));
    }

    public Map<String, MetricRecord<?>> getRecordMap() {
        List<MetricRecord<?>> collectedMetrics = service.getCollectedMetrics("test");
        return collectedMetrics.stream()
                .collect(Collectors.toMap(MetricRecord::getName, Function.identity()));
    }

}