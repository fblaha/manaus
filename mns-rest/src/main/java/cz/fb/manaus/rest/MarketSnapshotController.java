package cz.fb.manaus.rest;

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

@Controller
public class MarketSnapshotController {

    @Lazy
    @Autowired
    private BetManager manager;
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private BetActionDao actionDao;

    @RequestMapping(value = "/markets/{id}/snapshot", method = RequestMethod.POST)
    public ResponseEntity<?> pushMarketSnapshot(@PathVariable String id,
                                                @RequestHeader("MNS_BET_URL") Optional<String> betUrl,
                                                @RequestBody MarketSnapshotCrate snapshotCrate) {
        MarketPrices marketPrices = snapshotCrate.getPrices();
        marketDao.get(id).ifPresent(marketPrices::setMarket);
        List<Bet> bets = Optional.ofNullable(snapshotCrate.getBets()).orElse(Collections.emptyList());
        MarketSnapshot marketSnapshot = new MarketSnapshot(marketPrices, bets, Optional.empty());
        Set<String> myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty());
        if (betUrl.isPresent()) {
            manager.silentFire(marketSnapshot, myBets, betUrl);
        }
        return ResponseEntity.accepted().build();
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
}
