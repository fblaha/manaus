package cz.fb.manaus.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MetricsManager {

    @Autowired
    private MetricRegistry registry;

    public Stream<MetricRecord<?>> getMeterMetricRecords(String name) {
        Meter meter = registry.meter(name);
        return Stream.of(
                new MetricRecord<>(name + ".count", meter.getCount()),
                new MetricRecord<>(name + ".rate15", meter.getFifteenMinuteRate()),
                new MetricRecord<>(name + ".rate5", meter.getFiveMinuteRate()),
                new MetricRecord<>(name + ".rate1", meter.getOneMinuteRate()),
                new MetricRecord<>(name + ".meanRate", meter.getMeanRate()));
    }

    public Stream<MetricRecord<?>> getCounterMetricRecords(String name) {
        Counter counter = registry.counter(name);
        return Stream.of(new MetricRecord<>(name, counter.getCount()));
    }

    public MetricRegistry getRegistry() {
        return registry;
    }
}
