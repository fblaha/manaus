package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class LookAheadFilter implements MarketFilter {

    private final int lookAheadDays;

    @Autowired
    public LookAheadFilter(@Value(CoreLocalConfiguration.LOOK_AHEAD_EL) int lookAheadDays) {
        this.lookAheadDays = lookAheadDays;
    }

    @Override
    public boolean test(Market market) {
        Duration lookAhead = Duration.ofDays(lookAheadDays);
        Instant start = market.getEvent().getOpenDate().toInstant();
        long untilStart = Instant.now().until(start, ChronoUnit.MINUTES);
        return untilStart < lookAhead.toMinutes();
    }
}