package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Component
public class LookAheadFilter implements MarketFilter {

    private final int lookAheadDays;

    @Autowired
    public LookAheadFilter(@Value("#{systemEnvironment['MNS_LOOK_AHEAD'] ?: 7}") int lookAheadDays) {
        this.lookAheadDays = lookAheadDays;
    }

    @Override
    public boolean accept(Market market, Set<String> categoryBlacklist) {
        var lookAhead = Duration.ofDays(lookAheadDays);
        var start = market.getEvent().getOpenDate().toInstant();
        var untilStart = Instant.now().until(start, ChronoUnit.MINUTES);
        return untilStart < lookAhead.toMinutes();
    }
}
