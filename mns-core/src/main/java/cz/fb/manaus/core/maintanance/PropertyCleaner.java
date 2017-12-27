package cz.fb.manaus.core.maintanance;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Override
    public String getName() {
        return "propertyCleanup";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofMinutes(10);
    }

    @Override
    public void run() {
        int count = propertiesService.purgeExpired();
        metricRegistry.counter("purge.property").inc(count);
        log.log(Level.INFO, "''{0}'' expired properties purged", count);
    }
}
