package cz.fb.manaus.core.test;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.dao.MarketDao;
import cz.fb.manaus.core.dao.MarketPricesDao;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Competition;
import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.EventTest;
import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketPricesTest;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.RunnerPricesTest;
import cz.fb.manaus.core.model.RunnerTest;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.spring.ManausProfiles;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
@Profile(ManausProfiles.DB)
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
        var market = new Market();
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
        var betAction = BetAction.create(BetActionType.PLACE, DateUtils.addHours(new Date(), -5), new Price(2d, 2d, Side.LAY), market, 11);
        betAction.setProperties(new HashMap<>());
        betAction.setBetId(betId);
        return betAction;
    }

    private static Collection<Runner> getRunners() {
        var home = RunnerTest.create(HOME, HOME_NAME, 0, 1);
        var draw = RunnerTest.create(DRAW, DRAW_NAME, 0, 2);
        var away = RunnerTest.create(AWAY, AWAY_NAME, 0, 3);
        return List.of(draw, home, away);
    }

    public static Event newEvent(Date curr) {
        return EventTest.create("77", EVENT_NAME, curr, COUNTRY_CODE);
    }

    public static Market newMarket() {
        return newMarket(MARKET_ID, DateUtils.addHours(new Date(), 2), MATCH_ODDS);
    }

    public static RunnerPrices newBackRP(double currPrice, long selectionId, Double lastMatchedPrice) {
        return RunnerPricesTest.create(selectionId, List.of(
                new Price(currPrice, 100d, Side.BACK),
                new Price(1.4d, 100d, Side.BACK),
                new Price(1.3d, 100d, Side.BACK)), 10d, lastMatchedPrice);
    }

    public static MarketPrices newMarketPrices(int winnerCount, double bestBackPrice) {
        var market = newMarket();
        var runnerPrices = List.of(
                newBackRP(bestBackPrice, 1, 2.5d),
                newBackRP(bestBackPrice, 2, 2.5d),
                newBackRP(bestBackPrice, 3, 2.5d));
        return MarketPricesTest.create(winnerCount, market, runnerPrices, new Date());
    }

    public static MarketPrices newMarketPrices(Market market) {
        var home = newBackRP(2.5d, HOME, 3d);
        var draw = newBackRP(2.5d, DRAW, 3d);
        var away = newBackRP(2.5d, AWAY, 3d);
        return MarketPricesTest.create(1, market, List.of(home, draw, away), new Date());
    }

    public static SettledBet newSettledBet(double price, Side side) {
        var bet = SettledBet.create(CoreTestFactory.HOME, "Home", 2d, new Date(), new Price(price, 2d, side));
        var market = newMarket();
        var action = newBetAction("1", market);
        action.setMarketPrices(CoreTestFactory.newMarketPrices(market));
        bet.setBetAction(action);
        return bet;
    }

    public BetAction savePlaceAction(Bet unmatched, Market market) {
        var betAction = BetAction.create(BetActionType.PLACE, unmatched.getPlacedDate(),
                unmatched.getRequestedPrice(), market, unmatched.getSelectionId());
        betAction.setBetId(unmatched.getBetId());
        betActionDao.saveOrUpdate(betAction);
        return betAction;
    }


}