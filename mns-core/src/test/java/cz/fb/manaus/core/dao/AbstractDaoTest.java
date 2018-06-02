package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

abstract public class AbstractDaoTest extends AbstractDatabaseTestCase {

    public static final String BET_ID = "99999";
    public static final String SPARTA = "Sparta Praha - Banik Ostrava";
    public static final Map<String, String> PROPS = Map.of("reciprocal", "0.9", "lastMatched", "2");
    public static final String MARKET_ID = "33";

    @Autowired
    protected BetActionDao betActionDao;
    @Autowired
    protected MarketDao marketDao;
    @Autowired
    protected MarketPricesDao marketPricesDao;
    @Autowired
    protected SettledBetDao settledBetDao;
    @Autowired
    protected EntityManagerFactory factory;

    @After
    public void cleanUp() {
        marketDao.deleteMarkets(Date.from(Instant.now().plus(5_000, ChronoUnit.DAYS)));
    }

    protected BetAction createAndSaveBetAction(Market market, Date date, Map<String, String> values, String betId) {
        var betAction = new BetAction(BetActionType.PLACE, date, new Price(2d, 3d, Side.LAY), market, CoreTestFactory.DRAW);
        var marketPrices = CoreTestFactory.newMarketPrices(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        betAction.setMarketPrices(marketPrices);
        betAction.setProperties(values);
        betAction.setBetId(betId);
        betActionDao.saveOrUpdate(betAction);
        return betAction;
    }

    protected void createMarketWithSingleAction() {
        var date = Date.from(Instant.now().plus(5, ChronoUnit.HOURS));
        var market = newMarket(MARKET_ID, date, CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, addHours(date, -1), PROPS, BET_ID);
    }

    protected SettledBet createMarketWithSingleSettledBet() {
        var now = Date.from(Instant.now());
        var market = newMarket(MARKET_ID, now, CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        var actionDate = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
        var action = createAndSaveBetAction(market, actionDate, PROPS, BET_ID);
        var bet = new SettledBet(action.getSelectionId(), "x", 5, now, action.getPrice());
        bet.setBetAction(action);
        settledBetDao.saveOrUpdate(bet);
        return bet;
    }

    protected void createBet() {
        var current = new Date();
        var market = newMarket();
        var prices = new MarketPrices(1, market, createRPs(2.1d, 2.2d), new Date());
        prices.setTime(DateUtils.addHours(current, -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(prices);
        betActionDao.saveOrUpdate(createAction(market, prices, "1"));
        var prices2 = new MarketPrices(1, market, createRPs(2.3d, 2.5d), new Date());
        prices2.setTime(current);
        marketPricesDao.saveOrUpdate(prices2);
        betActionDao.saveOrUpdate(createAction(market, prices2, "2"));
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
    }

    private BetAction createAction(Market market, MarketPrices prices, String betId) {
        var place = new BetAction(BetActionType.PLACE, new Date(), new Price(2.2d, 2.1d, Side.LAY),
                market, CoreTestFactory.HOME, betId);
        place.setMarketPrices(prices);
        return place;
    }

    private List<RunnerPrices> createRPs(double price, double lastMatchedPrice) {
        return List.of(
                CoreTestFactory.newBackRP(price, CoreTestFactory.HOME, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.DRAW, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.AWAY, lastMatchedPrice));
    }

}
