package cz.fb.manaus.rest;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Optional.empty;

@Repository
@Profile(ManausProfiles.DB)
public class SettledBetLoader {

    private static final Logger log = Logger.getLogger(SettledBetLoader.class.getSimpleName());
    @Autowired
    private IntervalParser intervalParser;
    @Autowired
    private SettledBetDao settledBetDao;
    @Autowired
    private BetActionDao betActionDao;
    private final LoadingCache<String, List<SettledBet>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public List<SettledBet> load(String key) {
                    return loadFromDatabase(key);
                }
            });

    public List<SettledBet> load(String interval, boolean cache) {
        if (cache) {
            try {
                return this.cache.get(interval);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            return loadFromDatabase(interval);
        }
    }

    private List<SettledBet> loadFromDatabase(String interval) {
        var range = intervalParser.parse(Instant.now(), interval);
        var stopwatch = Stopwatch.createStarted();
        var settledBets = settledBetDao.getSettledBets(
                Optional.of(Date.from(range.lowerEndpoint())),
                Optional.of(Date.from(range.upperEndpoint())),
                empty(), OptionalInt.empty());
        var elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, "Settle bets loaded {0} in ''{1}'' seconds", elapsed);
        stopwatch.reset().start();
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS);
        log.log(Level.INFO, "Market prices loaded in ''{1}'' seconds", elapsed);
        return settledBets;
    }

}
