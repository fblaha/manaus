package cz.fb.manaus.betfair.task;

import com.google.common.collect.Lists;
import cz.fb.manaus.betfair.BetfairFacade;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.reactor.betting.BetManager;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;

@DatabaseComponent
public class MarketPriceScanner implements ProviderTask {

    private static final Logger log = Logger.getLogger(MarketPriceScanner.class.getSimpleName());

    @Autowired
    private MarketDao marketDao;
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private BetfairFacade betService;
    @Autowired
    private BetManager betManager;

    private Map<String, MarketSnapshot> getSnapshots(Set<String> ids) {
        try {
            return betService.getSnapshot(ids);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "Unable to fetch snapshots ''{0}''", ids);
            log.log(Level.SEVERE, "fix it!", e);
            return Collections.emptyMap();
        }
    }

    private boolean isOk(Market market) {
        boolean result = addSeconds(new Date(), 60).before(market.getEvent().getOpenDate());
        if (!result) {
            log.log(Level.INFO, "SCAN: skipping obsolete date ''{0}'' for ''{1}''", new Object[]{market.getEvent().getOpenDate(), market});
        }
        return result;
    }

    @Override
    public String getName() {
        return "betfair.price.scanner";
    }

    @Override
    public Duration getPauseDuration() {
        return Duration.ofMinutes(3);
    }

    @Override
    public void execute() {
        List<Market> allMarkets = marketDao.getMarkets(Optional.of(new Date()), Optional.empty(), OptionalInt.empty());
        int counter = 0;
        for (List<Market> marketGroup : Lists.partition(allMarkets, 6)) {
            List<Market> filtered = marketGroup.stream().filter(this::isOk).collect(toList());
            Set<String> ids = filtered.stream().map(Market::getId).collect(toSet());
            Map<String, MarketSnapshot> snapshots = getSnapshots(ids);
            for (Market market : filtered) {
                counter++;
                if (counter % 50 == 0) {
                    log.log(Level.INFO, getLogPrefix() + "processing ''{0}/{1}''", new Object[]{counter, allMarkets.size()});
                }
                MarketSnapshot snapshot = snapshots.get(market.getId());
                Set<String> myBets = betActionDao.getBetActionIds(market.getId(), OptionalLong.empty(), Optional.empty());
                if (snapshot == null) {
                    log.log(Level.WARNING, getLogPrefix() + "inactive market ''{0}''", market);
                    if (myBets.isEmpty()) {
                        log.log(Level.WARNING, getLogPrefix() + "removing inactive market ''{0}''", market);
                        marketDao.delete(market.getId());
                    }
                    continue;
                }
                snapshot.getMarketPrices().setMarket(market);

                betManager.silentFire(snapshot, myBets);
            }
        }
    }
}
