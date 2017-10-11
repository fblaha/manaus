package cz.fb.manaus.core.metrics;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DummyContributor implements MetricsContributor {
    @Override
    public Stream<MetricRecord> getMetricRecords() {
        return Stream.of(new MetricRecord("todo", 1000d));
    }
}
