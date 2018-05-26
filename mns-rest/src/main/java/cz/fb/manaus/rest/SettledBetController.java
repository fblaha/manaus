package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.settlement.SaveStatus;
import cz.fb.manaus.core.settlement.SettledBetSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import static java.util.Optional.empty;


@Controller
public class SettledBetController {

    @Autowired
    private SettledBetDao settledBetDao;
    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private IntervalParser intervalParser;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SettledBetSaver betSaver;
    @Autowired
    private MetricRegistry metricRegistry;


    @ResponseBody
    @RequestMapping(value = "/markets/{id}/bets", method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@PathVariable String id) {
        var settledBets = settledBetDao.getSettledBets(id, OptionalLong.empty(), empty());
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        return settledBets;
    }

    @ResponseBody
    @RequestMapping(value = "/bets", method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@RequestParam(defaultValue = "20") int maxResults) {
        var bets = settledBetDao.getSettledBets(empty(), Optional.empty(), empty(), OptionalInt.of(maxResults));
        betActionDao.fetchMarketPrices(bets.stream().map(SettledBet::getBetAction));
        return ImmutableList.copyOf(bets).reverse();
    }

    @ResponseBody
    @RequestMapping(value = "/bets/" + IntervalParser.INTERVAL, method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@PathVariable String interval,
                                           @RequestParam(required = false) Optional<String> projection,
                                           @RequestParam(required = false) Optional<String> namespace) {
        var range = intervalParser.parse(Instant.now(), interval);
        var from = Date.from(range.lowerEndpoint());
        var to = Date.from(range.upperEndpoint());
        var settledBets = settledBetDao.getSettledBets(Optional.of(from), Optional.of(to), empty(),
                OptionalInt.empty());
        if (projection.isPresent()) {
            settledBets = categoryService.filterBets(settledBets, projection.get(), BetCoverage.from(settledBets));
        }
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        return ImmutableList.copyOf(settledBets).reverse();
    }

    @ResponseBody
    @RequestMapping(value = "/stories/{betId}", method = RequestMethod.GET)
    public BetStory getBetStory(@PathVariable String betId) {
        var action = betActionDao.getBetAction(betId).get();
        betActionDao.fetchMarketPrices(action);
        return toBetStory(action);
    }

    @RequestMapping(value = "/bets", method = RequestMethod.POST)
    public ResponseEntity<?> addBet(@RequestParam String betId, @RequestBody SettledBet bet) {
        Objects.requireNonNull(betId, "betId==null");
        metricRegistry.counter("settled.bet.post").inc();
        if (betSaver.saveBet(betId, bet) == SaveStatus.NO_ACTION) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.accepted().build();
        }
    }

    private BetStory toBetStory(BetAction head) {
        var previous = betActionDao.getBetActions(head.getMarket().getId(),
                OptionalLong.of(head.getSelectionId()),
                Optional.of(head.getPrice().getSide()));
        previous = previous.stream()
                .filter(action -> head.getActionDate().after(action.getActionDate()))
                .collect(Collectors.toList());
        previous.forEach(betActionDao::fetchMarketPrices);
        return new BetStory(head, null, previous);
    }
}
