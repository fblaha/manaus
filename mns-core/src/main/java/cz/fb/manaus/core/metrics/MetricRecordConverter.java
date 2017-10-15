package cz.fb.manaus.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MetricRecordConverter {

    @Autowired
    private MetricRegistry metricRegistry;

    public Stream<MetricRecord<?>> getMeterMetricRecords(String name) {
        Meter meter = metricRegistry.meter(name);
        String prefix = "meter." + name;
        return Stream.of(
                new MetricRecord<>(prefix + ".count", meter.getCount()),
                new MetricRecord<>(prefix + ".rate15", meter.getFifteenMinuteRate()),
                new MetricRecord<>(prefix + ".rate5", meter.getFiveMinuteRate()),
                new MetricRecord<>(prefix + ".rate1", meter.getOneMinuteRate()),
                new MetricRecord<>(prefix + ".meanRate", meter.getMeanRate()));
    }

    public Stream<MetricRecord<?>> getCounterMetricRecords(String name) {
        Counter counter = metricRegistry.counter(name);
        return Stream.of(new MetricRecord<>("counter." + name, counter.getCount()));
    }


}
