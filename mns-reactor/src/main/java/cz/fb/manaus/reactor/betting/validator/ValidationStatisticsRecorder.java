package cz.fb.manaus.reactor.betting.validator;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO replace by metrics
@Component
public class ValidationStatisticsRecorder {

    public static final String VALIDATOR_STATS_PREFIX = "validator.stats.";
    private static final Logger log = Logger.getLogger(ValidationStatisticsRecorder.class.getSimpleName());
    private final Map<String, AtomicLong> passMap = new HashMap<>();
    private final Map<String, AtomicLong> failMap = new HashMap<>();
    @Autowired
    private Optional<PropertiesService> propertiesService;

    public void record(ValidationResult result, Side type, Validator validator) {
        Class<? extends Validator> validatorClass = validator.getClass();
        String recordKey = getName(type, validator);
        long cnt;
        if (result.isSuccess()) {
            cnt = passMap.computeIfAbsent(recordKey, k -> new AtomicLong(0)).incrementAndGet();
        } else {
            cnt = failMap.computeIfAbsent(recordKey, k -> new AtomicLong(0)).incrementAndGet();
        }
        if (cnt % getFrequency(cnt) == 0) {
            long pass = passMap.computeIfAbsent(recordKey, k -> new AtomicLong(0)).get();
            long fail = failMap.computeIfAbsent(recordKey, k -> new AtomicLong(0)).get();
            log.log(Level.INFO,
                    "pass count ''{0}'' fail count ''{1}'' for validator class ''{2}''",
                    new Object[]{pass, fail, validatorClass.getName()});
            propertiesService.ifPresent(svc -> svc.set(recordKey,
                    Joiner.on('-').join(pass, fail), Duration.ofDays(2)));
        }
    }

    private String getName(Side type, Validator validator) {
        return VALIDATOR_STATS_PREFIX + Joiner.on('.').join(type.name().toLowerCase(), validator.getName());
    }

    private long getFrequency(long cnt) {
        if (cnt <= 100) return 10;
        if (cnt <= 1000) return 100;
        if (cnt <= 5000) return 200;
        return 500;
    }

}
