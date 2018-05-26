package cz.fb.manaus.reactor.betting.validator;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationMetricsCollector {

    public static final String PREFIX = "validator.stats";
    @Autowired
    private MetricRegistry metricRegistry;

    public void updateMetrics(ValidationResult result, Side type, String validatorName) {
        var name = getName(type, result.isSuccess(), validatorName);
        metricRegistry.counter(name).inc();
    }

    private String getName(Side type, boolean pass, String validatorName) {
        return Joiner.on('.').join(PREFIX,
                type.name().toLowerCase(), validatorName,
                pass ? "pass" : "fail");
    }

}
