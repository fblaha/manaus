package cz.fb.manaus.core.maintanance;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Stopwatch;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Date.from;

@DatabaseComponent
public class MarketCleaner implements PeriodicMaintenanceTask {
    private static final Logger log = Logger.getLogger(MarketCleaner.class.getSimpleName());
    public static final String HIST_DAYS_EL = "#{systemEnvironment['MNS_MARKET_HISTORY_DAYS'] ?: 200}";
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private MetricRegistry metricRegistry;

    private final int marketHistoryDays;

    @Autowired
    public MarketCleaner(@Value(HIST_DAYS_EL) int marketHistoryDays) {
        this.marketHistoryDays = marketHistoryDays;
    }

    @Override
    public String getName() {
        return "marketCleanup";
    }

    @Override
    public Duration getPausePeriod() {
        return Duration.ofMinutes(15);
    }

    @Override
    public ConfigUpdate execute() {
        var stopwatch = Stopwatch.createUnstarted().start();
        var count = marketDao.deleteMarkets(from(Instant.now().minus(140, ChronoUnit.DAYS)));
        metricRegistry.counter("purge.market").inc(count);
        var elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, "DELETE_MARKETS: ''{0}'' obsolete markets removed in ''{1}'' seconds", new Object[]{count, elapsed});
        return ConfigUpdate.NOP;
    }
}
