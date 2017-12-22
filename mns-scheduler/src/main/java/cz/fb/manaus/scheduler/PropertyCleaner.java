package cz.fb.manaus.scheduler;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseComponent
public class PropertyCleaner implements PeriodicMaintenanceTask {
    private static final Logger log = Logger.getLogger(PropertyCleaner.class.getSimpleName());

    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    private MetricRegistry metricRegistry;

    @Scheduled(fixedDelay = DateUtils.MILLIS_PER_HOUR)
    public void purgeExpiredProperties() {
        int count = propertiesService.purgeExpired();
        metricRegistry.counter("purge.property").inc(count);
        log.log(Level.INFO, "''{0}'' expired properties purged", count);
    }

    @Override
    public String getName() {
        return "propertyCleanup";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofHours(1);
    }

    @Override
    public void run() {
        purgeExpiredProperties();
    }
}
