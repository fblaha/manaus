package cz.fb.manaus.reactor.betting;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import cz.fb.manaus.core.dao.AbstractDaoTest;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketSnapshot;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.TradedVolume;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
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
        BetCollector collector = new BetCollector();
        MarketSnapshot snapshot = new MarketSnapshot(marketPrices, bets, Optional.of(createTradedVolume(marketPrices)));
        bettor.onMarketSnapshot(snapshot, collector);
        assertThat(collector.getToPlace().size(), is(placeCount));
        assertThat(collector.getToUpdate().size(), is(updateCount));
        return collector;
    }

    private Map<Long, TradedVolume> createTradedVolume(MarketPrices marketPrices) {
        ListMultimap<Long, Price> result = LinkedListMultimap.create();
        for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {
            Double lastMatchedPrice = runnerPrices.getLastMatchedPrice();
            result.put(runnerPrices.getSelectionId(), new Price(lastMatchedPrice, 5d, null));
            result.put(runnerPrices.getSelectionId(),
                    new Price(roundingService.increment(lastMatchedPrice, 1).getAsDouble(), 5d, null));
            result.put(runnerPrices.getSelectionId(),
                    new Price(roundingService.decrement(lastMatchedPrice, 1).getAsDouble(), 5d, null));
        }
        return Maps.transformValues(result.asMap(), TradedVolume::new);
    }

    protected BetCollector checkPlace(MarketPrices marketPrices, int expectedCount, OptionalDouble expectedPrice) {
        BetCollector result = check(marketPrices, Collections.<Bet>emptyList(), expectedCount, 0);
        List<BetCommand> toPlace = result.getToPlace();
        if (expectedPrice.isPresent()) {
            for (BetCommand command : toPlace) {
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
        Price oldOne = new Price(oldPrice, 3.72d, type);
        Bet unmatchedHome = new Bet(BET_ID, MARKET_ID, HOME, oldOne, PLACED_DATE, 0d);
        Bet unmatchedDraw = new Bet(BET_ID + 1, MARKET_ID, DRAW, oldOne, PLACED_DATE, 0d);
        Bet unmatchedAway = new Bet(BET_ID + 2, MARKET_ID, AWAY, oldOne, PLACED_DATE, 0d);
        List<Bet> bets = Arrays.asList(unmatchedHome, unmatchedDraw, unmatchedAway);
        List<BetAction> actions = bets.stream()
                .map(bet -> coreTestFactory.savePlaceAction(bet, marketPrices.getMarket()))
                .collect(Collectors.toList());
        check(marketPrices, bets, placeCount, updateCount);
        actions.forEach(betActionDao::delete);
    }
}
