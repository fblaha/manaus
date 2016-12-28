package cz.fb.manaus.betfair.task;

import cz.fb.manaus.betfair.BetfairFacade;
import cz.fb.manaus.core.manager.MarketsUpdater;
import cz.fb.manaus.core.provider.ProviderTask;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

@DatabaseComponent
public class MarketScanner implements ProviderTask {

    private static final Logger log = Logger.getLogger(MarketScanner.class.getSimpleName());

    @Autowired
    private MarketsUpdater marketsUpdater;
    @Autowired
    private BetfairFacade betService;
    @Value(CoreLocalConfiguration.LOOK_AHEAD_EL)
    private int lookAheadDays;

    @Override
    public String getName() {
        return "betfair.market.update";
    }

    @Override
    public Duration getPauseDuration() {
        return Duration.ofMinutes(40);
    }

    @Override
    public void execute() {
        log.info("MARKET_SCANNER: Markets renewed.");
        Instant from = Instant.now();
        Instant to = from.plus(lookAheadDays, ChronoUnit.DAYS);
        betService.walkMarkets(Date.from(from), Date.from(to), marketsUpdater::checkAndSave);
    }
}
