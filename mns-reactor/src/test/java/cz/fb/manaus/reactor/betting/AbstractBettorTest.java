package cz.fb.manaus.reactor.betting;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import cz.fb.manaus.core.dao.AbstractDaoTest;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.fb.manaus.core.test.CoreTestFactory.AWAY;
import static cz.fb.manaus.core.test.CoreTestFactory.DRAW;
import static cz.fb.manaus.core.test.CoreTestFactory.HOME;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractBettorTest<T extends AbstractUpdatingBettor> extends AbstractDaoTest {

    public static Date PLACED_DATE = addHours(new Date(), -10);
    @Autowired
    protected ReactorTestFactory reactorTestFactory;
    @Autowired
    protected CoreTestFactory coreTestFactory;
    @Autowired
    protected RoundingService roundingService;
    @Autowired
    protected T bettor;

    protected BetCollector check(MarketPrices marketPrices, List<Bet> bets, int placeCount, int updateCount) {
        var collector = new BetCollector();
        var snapshot = MarketSnapshot.from(marketPrices, bets,
                Optional.of(createTradedVolume(marketPrices)));
        bettor.onMarketSnapshot(snapshot, collector, Optional.empty(), Set.of());
        assertThat(collector.getToPlace().size(), is(placeCount));
        assertThat(collector.getToUpdate().size(), is(updateCount));
        return collector;
    }

    private Map<Long, TradedVolume> createTradedVolume(MarketPrices marketPrices) {
        var result = LinkedListMultimap.<Long, Price>create();
        for (var runnerPrices : marketPrices.getRunnerPrices()) {
            var lastMatchedPrice = runnerPrices.getLastMatchedPrice();
            result.put(runnerPrices.getSelectionId(), new Price(lastMatchedPrice, 5d, null));
            result.put(runnerPrices.getSelectionId(),
                    new Price(roundingService.increment(lastMatchedPrice, 1).getAsDouble(), 5d, null));
            result.put(runnerPrices.getSelectionId(),
                    new Price(roundingService.decrement(lastMatchedPrice, 1).getAsDouble(), 5d, null));
        }
        return Maps.transformValues(result.asMap(), TradedVolume::new);
    }

    protected BetCollector checkPlace(MarketPrices marketPrices, int expectedCount, OptionalDouble expectedPrice) {
        var result = check(marketPrices, List.of(), expectedCount, 0);
        var toPlace = result.getToPlace();
        if (expectedPrice.isPresent()) {
            for (var command : toPlace) {
                assertThat(command.getBet().getRequestedPrice().getPrice(), is(expectedPrice.getAsDouble()));
            }
        }
        return result;
    }

    protected MarketPrices persistMarket(MarketPrices prices) {
        marketDao.saveOrUpdate(prices.getMarket());
        marketPricesDao.saveOrUpdate(prices);
        return prices;
    }

    protected void checkUpdate(MarketPrices marketPrices, double oldPrice, Side type, int placeCount, int updateCount) {
        var oldOne = new Price(oldPrice, 3.72d, type);
        var unmatchedHome = new Bet(BET_ID, MARKET_ID, HOME, oldOne, PLACED_DATE, 0d);
        var unmatchedDraw = new Bet(BET_ID + 1, MARKET_ID, DRAW, oldOne, PLACED_DATE, 0d);
        var unmatchedAway = new Bet(BET_ID + 2, MARKET_ID, AWAY, oldOne, PLACED_DATE, 0d);
        var bets = List.of(unmatchedHome, unmatchedDraw, unmatchedAway);
        var actions = bets.stream()
                .map(bet -> coreTestFactory.savePlaceAction(bet, marketPrices.getMarket()))
                .collect(Collectors.toList());
        check(marketPrices, bets, placeCount, updateCount);
        actions.forEach(betActionDao::delete);
    }
}
