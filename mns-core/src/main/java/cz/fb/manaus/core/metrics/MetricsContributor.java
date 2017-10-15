package cz.fb.manaus.core.metrics;

import java.util.stream.Stream;

public interface MetricsContributor {

    Stream<MetricRecord<?>> getMetricRecords();

}
