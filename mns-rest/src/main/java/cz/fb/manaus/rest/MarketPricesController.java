package cz.fb.manaus.rest;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.reactor.betting.BetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

@Controller
public class MarketPricesController {

    @Autowired
    private MarketPricesDao marketPricesDao;
    @Autowired
    private MarketDao marketDao;
    @Lazy
    @Autowired
    private BetManager manager;
    @Autowired
    private BetActionDao actionDao;

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/prices", method = RequestMethod.GET)
    public List<MarketPrices> getMarketPrices(@PathVariable String id) {
        return marketPricesDao.getPrices(id);
    }

    @RequestMapping(value = "/markets/{id}/prices", method = RequestMethod.POST)
    public ResponseEntity<?> pushMarketPrices(@PathVariable String id,
                                              @RequestParam Optional<String> betUrl,
                                              @RequestBody MarketPrices marketPrices) {
        marketDao.get(id).ifPresent(marketPrices::setMarket);
        MarketSnapshot marketSnapshot = new MarketSnapshot(marketPrices, Collections.emptyList(), Optional.empty());
        Set<String> myBets = actionDao.getBetActionIds(id, OptionalLong.empty(), Optional.empty());
        betUrl.ifPresent(url -> manager.silentFire(marketSnapshot, myBets));
        return ResponseEntity.accepted().build();
    }

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/prices/{selectionId:\\d+}", method = RequestMethod.GET)
    public List<RunnerPrices> getRunnerPrices(@PathVariable String id, @PathVariable int selectionId) {
        return marketPricesDao.getRunnerPrices(id, selectionId, OptionalInt.empty());
    }
}
