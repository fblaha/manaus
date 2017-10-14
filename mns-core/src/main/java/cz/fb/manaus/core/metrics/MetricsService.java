package cz.fb.manaus.core.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MetricsService {

    @Autowired(required = false)
    private final List<MetricsContributor> contributors = Collections.emptyList();

    public List<MetricRecord> getCollectedMetrics(String prefix) {
        return contributors.stream().flatMap(MetricsContributor::getMetricRecords)
                .filter(record -> record.getName().startsWith(prefix))
                .sorted(Comparator.comparing(MetricRecord::getName))
                .collect(Collectors.toList());
    }

}
