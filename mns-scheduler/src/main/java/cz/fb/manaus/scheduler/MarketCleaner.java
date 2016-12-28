package cz.fb.manaus.scheduler;

import com.google.common.base.Stopwatch;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.spring.DatabaseComponent;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Date.from;

@DatabaseComponent
public class MarketCleaner {
    private static final Logger log = Logger.getLogger(MarketCleaner.class.getSimpleName());
    @Autowired
    private MarketDao marketDao;

    @Scheduled(fixedDelay = 10 * DateUtils.MILLIS_PER_MINUTE)
    public void purgeMarkets() {
        Stopwatch stopwatch = Stopwatch.createUnstarted().start();
        int count = marketDao.deleteMarkets(from(Instant.now().minus(140, ChronoUnit.DAYS)));
        long elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, "DELETE_MARKETS: ''{0}'' obsolete markets removed in ''{1}'' seconds", new Object[]{count, elapsed});
    }

}
