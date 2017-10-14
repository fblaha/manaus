package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.MoreObjects;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsContributor;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.CollectedBets;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.reactor.betting.BetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


@Controller
public class MarketSnapshotController implements MetricsContributor {

    private static final Logger log = Logger.getLogger(MarketSnapshotController.class.getSimpleName());

    @Autowired
    private BetManager manager;
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private BetActionDao actionDao;
    @Autowired
    private MetricRegistry metricRegistry;

    @RequestMapping(value = "/markets/{id}/snapshot", method = RequestMethod.POST)
    public ResponseEntity<?> pushMarketSnapshot(@PathVariable String id,
                                                @RequestBody MarketSnapshotCrate snapshotCrate) {
        metricRegistry.meter("market.snapshot").mark();
        MarketPrices marketPrices = snapshotCrate.getPrices();
        log.log(Level.INFO, "Market snapshot for ''{0}'' recieved", id);
        marketDao.get(id).ifPresent(marketPrices::setMarket);
        List<Bet> bets = Optional.ofNullable(snapshotCrate.getBets()).orElse(Collections.emptyList());
        MarketSnapshot marketSnapshot = new MarketSnapshot(marketPrices, bets, Optional.empty());
        Set<String> myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty());
        CollectedBets collectedBets = manager.silentFire(marketSnapshot, myBets);
        if (!collectedBets.isEmpty()) {
            return ResponseEntity.ok(collectedBets);
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public Stream<MetricRecord<?>> getMetricRecords() {
        return getMeterMetricRecords("market.snapshot", metricRegistry);
    }
}

class MarketSnapshotCrate {
    private MarketPrices prices;
    private List<Bet> bets;

    public MarketPrices getPrices() {
        return prices;
    }

    public void setPrices(MarketPrices prices) {
        this.prices = prices;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("prices", prices)
                .add("bets", bets)
                .toString();
    }
}
