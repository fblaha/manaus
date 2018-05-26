package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


@Controller
public class MarketSnapshotController {

    private static final Logger log = Logger.getLogger(MarketSnapshotController.class.getSimpleName());

    @Autowired
    private BetManager manager;
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private BetActionDao actionDao;
    @Autowired
    private MetricRegistry metricRegistry;
    @Autowired
    private MatchedBetMetricUpdater betMetricUpdater;

    @RequestMapping(value = "/markets/{id}/snapshot", method = RequestMethod.POST)
    public ResponseEntity<?> pushMarketSnapshot(@PathVariable String id,
                                                @RequestBody MarketSnapshotCrate snapshotCrate) {
        validateMarket(snapshotCrate);
        metricRegistry.meter("market.snapshot.post").mark();
        try {
            var marketPrices = snapshotCrate.getPrices();
            marketDao.get(id).ifPresent(marketPrices::setMarket);
            logMarket(marketPrices);
            var bets = Optional.ofNullable(snapshotCrate.getBets()).orElse(List.of());
            betMetricUpdater.update(snapshotCrate.getScanTime(), bets);
            var marketSnapshot = new MarketSnapshot(marketPrices, bets,
                    Optional.empty());
            var myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty());
            var collectedBets = manager.fire(marketSnapshot, myBets,
                    Optional.ofNullable(snapshotCrate.getMoney()),
                    Optional.ofNullable(snapshotCrate.getCategoryBlacklist()).orElse(Set.of()));
            if (!collectedBets.isEmpty()) {
                return ResponseEntity.ok(collectedBets);
            }
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            metricRegistry.counter("_SNAPSHOT_ERROR_").inc();
            logException(snapshotCrate, e);
            throw e;
        }
    }

    private void logMarket(MarketPrices marketPrices) {
        var market = marketPrices.getMarket();
        log.log(Level.INFO, "Market snapshot for ''{0}'' received",
                Joiner.on(" / ").join(market.getEvent().getName(), market.getName(), market.getId()));
    }

    private void validateMarket(MarketSnapshotCrate snapshotCrate) {
        Objects.requireNonNull(snapshotCrate.getPrices());
        Objects.requireNonNull(snapshotCrate.getPrices().getRunnerPrices());
        Preconditions.checkState(!snapshotCrate.getPrices().getRunnerPrices().isEmpty());
    }

    private void logException(MarketSnapshotCrate snapshot, RuntimeException e) {
        log.log(Level.SEVERE, "Error emerged for ''{0}''", snapshot);
        log.log(Level.SEVERE, "fix it!", e);
    }
}

class MarketSnapshotCrate {
    private MarketPrices prices;
    private List<Bet> bets;
    private Set<String> categoryBlacklist;
    private AccountMoney money;
    private int scanTime;

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

    public AccountMoney getMoney() {
        return money;
    }

    public void setMoney(AccountMoney money) {
        this.money = money;
    }

    public int getScanTime() {
        return scanTime;
    }

    public void setScanTime(int scanTime) {
        this.scanTime = scanTime;
    }

    public Set<String> getCategoryBlacklist() {
        return categoryBlacklist;
    }

    public void setCategoryBlacklist(Set<String> categoryBlacklist) {
        this.categoryBlacklist = categoryBlacklist;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("prices", prices)
                .add("bets", bets)
                .add("categoryBlacklist", categoryBlacklist)
                .add("money", money)
                .add("scanTime", scanTime)
                .toString();
    }
}
