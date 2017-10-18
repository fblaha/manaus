package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import cz.fb.manaus.core.model.Bet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MatchedBetMetricUpdater {

    public static final String METRIC_NAME = "bet.matched.count";
    private final AtomicLong lastScan = new AtomicLong(0);

    @Autowired
    private MetricRegistry metricRegistry;

    public void update(long scanTime, List<Bet> bets) {
        long last = lastScan.getAndSet(scanTime);
        if (last > 0 && last != scanTime) {
            metricRegistry.remove(METRIC_NAME);
        }
        long currentCount = bets.stream().filter(Bet::isHalfMatched).count();
        if (currentCount > 0) {
            metricRegistry.counter(METRIC_NAME).inc(currentCount);
        }
    }
}
