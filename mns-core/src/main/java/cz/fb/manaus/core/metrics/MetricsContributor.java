package cz.fb.manaus.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.stream.Stream;

public interface MetricsContributor {

    default Stream<MetricRecord<?>> getMeterMetricRecords(String name, MetricRegistry metricRegistry) {
        Meter meter = metricRegistry.meter(name);
        return Stream.of(
                new MetricRecord<>(name + ".count", meter.getCount()),
                new MetricRecord<>(name + ".rate15", meter.getFifteenMinuteRate()),
                new MetricRecord<>(name + ".rate5", meter.getFiveMinuteRate()),
                new MetricRecord<>(name + ".rate1", meter.getOneMinuteRate()),
                new MetricRecord<>(name + ".meanRate", meter.getMeanRate()));
    }

    default Stream<MetricRecord<?>> getCounterMetricRecords(String name, MetricRegistry metricRegistry) {
        Counter counter = metricRegistry.counter(name);
        return Stream.of(new MetricRecord<>(name, counter.getCount()));
    }

    Stream<MetricRecord<?>> getMetricRecords();

}
