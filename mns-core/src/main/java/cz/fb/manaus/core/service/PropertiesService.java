package cz.fb.manaus.core.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

@Component
public class PropertiesService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

    public Optional<String> get(String name) {
        return Optional.empty();
    }

    public void set(String name, String value, Duration ttl) {
    }

    public Map<String, String> list(Optional<String> prefix) {
        return Collections.emptyMap();
    }

    public int delete(String prefix) {
        return 0;
    }

    public void setInstant(String name, Instant instant, Duration validPeriod) {
        setInstant(name, instant, validPeriod, ZoneId.systemDefault());
    }

    void setInstant(String name, Instant instant, Duration validPeriod, ZoneId zone) {
        DateTimeFormatter withZone = FORMATTER.withZone(zone);
        set(name, withZone.format(instant), validPeriod);
    }

    public Optional<Instant> getInstant(String name) {
        return Optional.empty();
    }

    public OptionalDouble getDouble(String name) {
        return OptionalDouble.empty();
    }
}
