package cz.fb.manaus.matchbook.task;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.manager.MarketsUpdater;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.matchbook.MatchbookService;
import cz.fb.manaus.reactor.betting.BetEndpoint;
import cz.fb.manaus.reactor.betting.BetManager;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Logger;

@DatabaseComponent
public class MatchbookScanner implements ProviderTask {

    private static final Logger log = Logger.getLogger(MatchbookScanner.class.getSimpleName());

    @Autowired
    private MarketsUpdater marketsUpdater;
    @Autowired
    private BetActionDao actionDao;
    @Autowired
    private MatchbookService betService;
    @Autowired
    private BetManager manager;
    @Value(CoreLocalConfiguration.LOOK_AHEAD_EL)
    private int lookAheadDays;

    @Override
    public String getName() {
        return "matchbook.scan";
    }

    @Override
    public Duration getPauseDuration() {
        return Duration.ofMinutes(10);
    }

    @Override
    public void execute() {
        log.info("Markets renewed.");
        Instant from = Instant.now();
        Instant to = from.plus(lookAheadDays, ChronoUnit.DAYS);
        betService.walkMarkets(from, to, this::saveAndFire);
    }

    private void saveAndFire(MarketSnapshot snapshot) {
        MarketPrices prices = snapshot.getMarketPrices();
        Market market = prices.getMarket();
        if (marketsUpdater.checkAndSave(market)) {
            Set<String> myBets = actionDao.getBetActionIds(market.getId(), OptionalLong.empty(), Optional.empty());
            manager.silentFire(snapshot, myBets, BetEndpoint.devNull());
        }
    }
}
