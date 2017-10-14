package cz.fb.manaus.reactor.betting.validator;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsContributor;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class ValidationMetricsCollector implements MetricsContributor {

    private final Set<String> names = ConcurrentHashMap.newKeySet();

    @Autowired
    private MetricRegistry registry;

    public void record(ValidationResult result, Side type, Validator validator) {
        String name = getName(type, result.isSuccess(), validator);
        names.add(name);
        Counter counter = registry.counter(name);
        counter.inc();
    }

    private String getName(Side type, boolean pass, Validator validator) {
        return Joiner.on('.').join("validator.stats",
                type.name().toLowerCase(), validator.getName(),
                pass ? "pass" : "fail");
    }

    @Override
    public Stream<MetricRecord<?>> getMetricRecords() {
        return names.stream().flatMap(name -> getCounterMetricRecords(name, registry));
    }
}
