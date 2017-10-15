package cz.fb.manaus.rest;

import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.metrics.MetricRecord;
import cz.fb.manaus.core.metrics.MetricsContributor;
import cz.fb.manaus.core.metrics.MetricsManager;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.reactor.betting.action.ActionSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Stream;

import static java.util.Optional.empty;

@Controller
public class BetActionController implements MetricsContributor {

    public static final String METRIC_NAME = "put.action.betId";
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private MarketDao marketDao;
    @Autowired
    private MarketPricesDao marketPricesDao;
    @Autowired
    private ActionSaver actionSaver;
    @Autowired
    private MetricsManager metricsManager;

    @ResponseBody
    @RequestMapping(value = "/markets/{id}/actions", method = RequestMethod.GET)
    public List<BetAction> getBetActions(@PathVariable String id) {
        List<BetAction> actions = betActionDao.getBetActions(id, OptionalLong.empty(), empty());
        betActionDao.fetchMarketPrices(actions.stream());
        return actions;
    }

    @ResponseBody
    @RequestMapping(value = "/actions", method = RequestMethod.GET)
    public List<BetAction> getBetActions(@RequestParam(defaultValue = "20") int maxResults) {
        List<BetAction> actions = betActionDao.getBetActions(OptionalInt.of(maxResults));
        betActionDao.fetchMarketPrices(actions.stream());
        return ImmutableList.copyOf(actions).reverse();
    }

    @RequestMapping(value = "/markets/{id}/actions", method = RequestMethod.POST)
    public ResponseEntity<?> addAction(@PathVariable String id,
                                       @RequestParam int priceId,
                                       @RequestBody BetAction action) {
        Market market = marketDao.get(id)
                .orElseThrow(IllegalArgumentException::new);
        action.setMarket(market);
        betActionDao.saveOrUpdate(action);
        marketPricesDao.get(priceId).ifPresent(action::setMarketPrices);
        betActionDao.saveOrUpdate(action);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/actions/{id}/betId", method = RequestMethod.PUT)
    public ResponseEntity<?> setBetId(@PathVariable int id,
                                      @RequestBody String betId) {
        int changedRows = actionSaver.setBetId(betId, id);
        metricsManager.getRegistry().meter(METRIC_NAME).mark(changedRows);
        if (changedRows > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public Stream<MetricRecord<?>> getMetricRecords() {
        return metricsManager.getMeterMetricRecords(METRIC_NAME);
    }
}
