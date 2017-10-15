package cz.fb.manaus.core.service;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricRecordConverter;
import cz.fb.manaus.core.metrics.MetricsContributor;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@DatabaseComponent
public class PeriodicTaskService implements MetricsContributor {

    public static final String PREFIX = "periodic.task.timestamp.";
    private static final Logger log = Logger.getLogger(PeriodicTaskService.class.getSimpleName());
    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    private MetricRegistry registry;
    @Autowired
    private MetricRecordConverter converter;

    boolean isRefreshRequired(String taskName, Duration pauseDuration) {
        Optional<Instant> timestamp = propertiesService.getInstant(getTimestampPropertyName(taskName));
        if (timestamp.isPresent()) {
            Duration actualDuration = Duration.between(timestamp.get(), Instant.now());
            Preconditions.checkState(!actualDuration.isNegative());
            if (actualDuration.toMillis() < pauseDuration.toMillis()) {
                Duration remainingTime = pauseDuration.minus(actualDuration);
                log.log(Level.INFO, getLogPrefix(taskName) + "task expires after ''{0}''", remainingTime);
                return false;
            }
        }
        return true;
    }

    private String getLogPrefix(String taskName) {
        return String.format("PERIODIC_TASK(%s): ", taskName);
    }

    private String getTimestampPropertyName(String taskName) {
        return PREFIX + taskName;
    }

    void setTimestamp(String taskName, Instant date) {
        log.log(Level.INFO, getLogPrefix(taskName) + "timestamp updated");
        propertiesService.setInstant(getTimestampPropertyName(taskName), date, Duration.ofDays(2));
    }

    public void runIfExpired(String taskName, Duration pauseDuration, Runnable task) {
        if (isRefreshRequired(taskName, pauseDuration)) {
            markUpdated(taskName);
            registry.counter("tasks.executed").inc();
            task.run();
        }
    }

    public void markUpdated(String taskName) {
        setTimestamp(taskName, Instant.now());
    }

    @Override
    public Stream<MetricRecord<?>> getMetricRecords() {
        return converter.getCounterMetricRecords("tasks.executed");
    }
}
