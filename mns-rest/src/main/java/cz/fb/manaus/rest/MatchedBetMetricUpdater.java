package cz.fb.manaus.rest;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingWindowReservoir;
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

    private Histogram newHistogram() {
        return new Histogram(new SlidingWindowReservoir(100));
    }

    public void update(long scanTime, List<Bet> bets) {
        long last = lastScan.getAndSet(scanTime);
        if (last > 0 && last != scanTime) {
            long lastCount = metricRegistry.counter(METRIC_NAME).getCount();
            metricRegistry.remove(METRIC_NAME);
            metricRegistry.histogram("bet.matched", this::newHistogram).update(lastCount);
        }
        long currentCount = bets.stream().filter(Bet::isHalfMatched).count();
        if (currentCount > 0) {
            metricRegistry.counter(METRIC_NAME).inc(currentCount);
        }
    }
}
