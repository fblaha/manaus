package cz.fb.manaus.core.service;

import com.google.common.base.Preconditions;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseComponent
public class PeriodicTaskService {

    public static final String PREFIX = "periodic.task.timestamp.";
    private static final Logger log = Logger.getLogger(PeriodicTaskService.class.getSimpleName());
    @Autowired
    private PropertiesService propertiesService;

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
            task.run();
        }
    }

    public void markUpdated(String taskName) {
        setTimestamp(taskName, Instant.now());
    }

}
