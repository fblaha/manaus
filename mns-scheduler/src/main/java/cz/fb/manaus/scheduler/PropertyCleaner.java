package cz.fb.manaus.scheduler;

import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.spring.DatabaseComponent;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.logging.Level;
import java.util.logging.Logger;

@DatabaseComponent
public class PropertyCleaner {
    private static final Logger log = Logger.getLogger(PropertyCleaner.class.getSimpleName());

    @Autowired
    private PropertiesService propertiesService;

    @Scheduled(fixedDelay = DateUtils.MILLIS_PER_HOUR)
    public void purgeExpiredProperties() {
        int count = propertiesService.purgeExpired();
        log.log(Level.INFO, "''{0}'' expired properties purged", count);
    }
}
