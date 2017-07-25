package cz.fb.manaus.rest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.SettledBetDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.RunnerPrices;
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


    @ResponseBody
    @RequestMapping(value = "/markets/{id}/bets", method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@PathVariable String id) {
        List<SettledBet> settledBets = settledBetDao.getSettledBets(id, OptionalLong.empty(), empty());
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        return settledBets;
    }

    @ResponseBody
    @RequestMapping(value = "/bets", method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@RequestParam(defaultValue = "20") int maxResults) {
        List<SettledBet> bets = settledBetDao.getSettledBets(empty(), Optional.empty(), empty(), OptionalInt.of(maxResults));
        betActionDao.fetchMarketPrices(bets.stream().map(SettledBet::getBetAction));
        return ImmutableList.copyOf(bets).reverse();
    }

    @ResponseBody
    @RequestMapping(value = "/bets/" + IntervalParser.INTERVAL, method = RequestMethod.GET)
    public List<SettledBet> getSettledBets(@PathVariable String interval,
                                           @RequestParam(required = false) Optional<String> projection,
                                           @RequestParam(required = false) Optional<String> namespace) {
        Range<Instant> range = intervalParser.parse(Instant.now(), interval);
        Date from = Date.from(range.lowerEndpoint());
        Date to = Date.from(range.upperEndpoint());
        List<SettledBet> settledBets = settledBetDao.getSettledBets(Optional.of(from), Optional.of(to), empty(),
                OptionalInt.empty());
        if (projection.isPresent()) {
            settledBets = categoryService.filterBets(settledBets, projection.get(),
                    namespace, BetCoverage.from(settledBets));
        }
        betActionDao.fetchMarketPrices(settledBets.stream().map(SettledBet::getBetAction));
        return ImmutableList.copyOf(settledBets).reverse();
    }

    @ResponseBody
    @RequestMapping(value = "/stories/{betId}", method = RequestMethod.GET)
    public BetStory getBetStory(@PathVariable String betId) {
        BetAction action = betActionDao.getBetAction(betId).get();
        betActionDao.fetchMarketPrices(action);
        return toBetStory(action);
    }

    @RequestMapping(value = "/bets/{betId}", method = RequestMethod.POST)
    ResponseEntity<?> add(@PathVariable String betId, @RequestBody SettledBet bet) {
        if (betSaver.saveBet(betId, bet) == SaveStatus.NO_ACTION) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    private BetStory toBetStory(BetAction head) {
        RunnerPrices runnerPrices = null;
        List<BetAction> previous = betActionDao.getBetActions(head.getMarket().getId(),
                OptionalLong.of(head.getSelectionId()),
                Optional.of(head.getPrice().getSide()));
        previous = previous.stream()
                .filter(action -> head.getActionDate().after(action.getActionDate()))
                .collect(Collectors.toList());
        previous.forEach(betActionDao::fetchMarketPrices);
        return new BetStory(head, runnerPrices, previous);
    }
}
