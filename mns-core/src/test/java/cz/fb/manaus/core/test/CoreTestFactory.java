package cz.fb.manaus.core.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Competition;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.spring.DatabaseComponent;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@DatabaseComponent
public class CoreTestFactory {

    public static final String MARKET_ID = "44";
    public static final long HOME = 1_000;
    public static final long DRAW = 1_001;
    public static final long AWAY = 1_002;
    public static final String MATCH_ODDS = "Match Odds";
    public static final String HOME_NAME = "Home";
    public static final String DRAW_NAME = "Draw";
    public static final String AWAY_NAME = "Away";
    public static final double AMOUNT = 44d;
    public static final String EVENT_NAME = "Manaus FC  Rio Negro AM";
    public static final String COUNTRY_CODE = "BR";

    @Autowired
    private MarketDao dao;
    @Autowired
    private MarketPricesDao marketPricesDao;
    @Autowired
    private BetActionDao betActionDao;


    public static Market newMarket(String id, Date curr, String name) {
        Market market = new Market();
        market.setId(id);
        market.setName(name);
        market.setMatchedAmount(AMOUNT);
        market.setBspMarket(false);
        market.setInPlay(false);

        market.setEventType(new EventType("1", "Soccer"));
        market.setCompetition(new Competition("7", "UA League"));
        market.setEvent(newEvent(curr));
        market.setRunners(getRunners());
        market.setType("MATCH_ODDS");
        return market;
    }

    public static BetAction newBetAction(String betId, Market market) {
        BetAction betAction = new BetAction(BetActionType.PLACE, DateUtils.addHours(new Date(), -5), new Price(2d, 2d, Side.LAY), market, 11);
        betAction.setProperties(new HashMap<>());
        betAction.setBetId(betId);
        return betAction;
    }

    private static Collection<Runner> getRunners() {
        Runner home = new Runner(HOME, HOME_NAME, 0, 1);
        Runner draw = new Runner(DRAW, DRAW_NAME, 0, 2);
        Runner away = new Runner(AWAY, AWAY_NAME, 0, 3);
        return Arrays.asList(draw, home, away);
    }

    public static Event newEvent(Date curr) {
        return new Event("77", EVENT_NAME, curr, COUNTRY_CODE);
    }

    public static Market newMarket() {
        return newMarket(MARKET_ID, DateUtils.addHours(new Date(), 2), MATCH_ODDS);
    }

    public static RunnerPrices newBackRP(double currPrice, long selectionId, Double lastMatchedPrice) {
        return new RunnerPrices(selectionId, ImmutableList.of(
                new Price(currPrice, 100d, Side.BACK),
                new Price(1.4d, 100d, Side.BACK),
                new Price(1.3d, 100d, Side.BACK)), 10d, lastMatchedPrice);
    }

    public static MarketPrices newMarketPrices(int winnerCount, double bestBackPrice) {
        Market market = newMarket();
        List<RunnerPrices> runnerPrices = Lists.newArrayList(
                newBackRP(bestBackPrice, 1, 2.5d),
                newBackRP(bestBackPrice, 2, 2.5d),
                newBackRP(bestBackPrice, 3, 2.5d));
        return new MarketPrices(winnerCount, market, runnerPrices);
    }

    public static MarketPrices newMarketPrices(Market market) {
        RunnerPrices home = newBackRP(2.5d, HOME, 3d);
        RunnerPrices draw = newBackRP(2.5d, DRAW, 3d);
        RunnerPrices away = newBackRP(2.5d, AWAY, 3d);
        return new MarketPrices(1, market, Arrays.asList(home, draw, away), new Date());
    }

    public static SettledBet newSettledBet(double price, Side side) {
        SettledBet bet = new SettledBet(CoreTestFactory.HOME, "Home", 2d, DateUtils.addDays(new Date(), -1), new Date(), new Price(price, 2d, side));
        Market market = newMarket();
        BetAction action = newBetAction("1", market);
        action.setMarketPrices(CoreTestFactory.newMarketPrices(market));
        bet.setBetAction(action);
        return bet;
    }

    public BetAction savePlaceAction(Bet unmatched, Market market) {
        BetAction betAction = new BetAction(BetActionType.PLACE, unmatched.getPlacedDate(),
                unmatched.getRequestedPrice(), market, unmatched.getSelectionId(), unmatched.getBetId());
        betActionDao.saveOrUpdate(betAction);
        return betAction;
    }


}