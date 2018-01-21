package cz.fb.manaus.core.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PropertiesService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

    private final Map<String, String> storage = new ConcurrentHashMap<>();

    public Optional<String> get(String name) {
        return Optional.ofNullable(storage.get(name));
    }

    public void set(String name, String value, Duration ttl) {
        storage.put(name, value);
    }

    public Map<String, String> list(Optional<String> prefix) {
        return storage.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix.orElse("")))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public int delete(Optional<String> prefix) {
        Set<String> keys = storage.keySet().stream()
                .filter(k -> k.startsWith(prefix.orElse("")))
                .collect(Collectors.toSet());
        storage.keySet().removeAll(keys);
        return keys.size();
    }

    public void setInstant(String name, Instant instant, Duration validPeriod) {
        setInstant(name, instant, validPeriod, ZoneId.systemDefault());
    }

    void setInstant(String name, Instant instant, Duration validPeriod, ZoneId zone) {
        DateTimeFormatter withZone = FORMATTER.withZone(zone);
        set(name, withZone.format(instant), validPeriod);
    }

    public Optional<Instant> getInstant(String name) {
        return get(name).map(d -> Instant.from(FORMATTER.parse(d)));
    }

    public OptionalDouble getDouble(String name) {
        Optional<Double> value = get(name).map(Double::parseDouble);
        if (value.isPresent()) {
            return OptionalDouble.of(value.get());
        } else {
            return OptionalDouble.empty();
        }
    }
}
