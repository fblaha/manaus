package cz.fb.manaus.rest;

import com.google.common.base.MoreObjects;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.reactor.betting.BetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class MarketSnapshotController {

    public static final String MNS_BET_URL = "MNS_BET_URL";
    public static final String MNS_AUTH_TOKEN = "MNS_AUTH_TOKEN";
    private static final Logger log = Logger.getLogger(MarketSnapshotController.class.getSimpleName());

    @Lazy
    @Autowired
    private BetManager manager;
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private BetActionDao actionDao;

    @RequestMapping(value = "/markets/{id}/snapshot", method = RequestMethod.POST)
    public ResponseEntity<?> pushMarketSnapshot(@PathVariable String id,
                                                @RequestHeader(MNS_BET_URL) Optional<String> betUrl,
                                                @RequestHeader(MNS_AUTH_TOKEN) Optional<String> authToken,
                                                @RequestBody MarketSnapshotCrate snapshotCrate) {
        MarketPrices marketPrices = snapshotCrate.getPrices();
        log.log(Level.INFO, "Market snapshot for ''{0}'' recieved");
        marketDao.get(id).ifPresent(marketPrices::setMarket);
        List<Bet> bets = Optional.ofNullable(snapshotCrate.getBets()).orElse(Collections.emptyList());
        MarketSnapshot marketSnapshot = new MarketSnapshot(marketPrices, bets, Optional.empty());
        Set<String> myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty());
        if (betUrl.isPresent() && authToken.isPresent()) {
            manager.silentFire(marketSnapshot, myBets, betUrl);
        }
        if (!betUrl.isPresent()) {
            logMissingHeader(MNS_BET_URL);
        }
        if (!authToken.isPresent()) {
            logMissingHeader(MNS_AUTH_TOKEN);
        }
        return ResponseEntity.accepted().build();
    }

    private void logMissingHeader(String header) {
        log.log(Level.WARNING, "Missing ''{0}'' header", header);
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
