package cz.fb.manaus.core.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import org.hamcrest.core.Is;
import org.hibernate.SessionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

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

@ActiveProfiles("cache-stats")
abstract public class AbstractDaoTest extends AbstractDatabaseTestCase {

    public static final String BET_ID = "99999";
    public static final String SPARTA = "Sparta Praha - Banik Ostrava";
    public static final ImmutableSet<String> TAGS = ImmutableSet.of("tag1", "tag2", "tag3");
    public static final Map<String, String> PROPS = ImmutableMap.of("reciprocal", "0.9", "lastMatched", "2");
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

    protected SessionFactory getSessionFactory() {
        return factory.unwrap(SessionFactory.class);
    }

    protected BetAction createAndSaveBetAction(Market market, Date date, Map<String, String> values, String betId) {
        BetAction betAction = new BetAction(BetActionType.PLACE, date, new Price(2d, 3d, Side.LAY), market, CoreTestFactory.DRAW);
        MarketPrices marketPrices = CoreTestFactory.newMarketPrices(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        betAction.setMarketPrices(marketPrices);
        betAction.setProperties(values);
        betAction.setBetId(betId);
        betAction.setTags(TAGS);
        betActionDao.saveOrUpdate(betAction);
        return betAction;
    }

    protected void createMarketWithSingleAction() {
        Date date = Date.from(Instant.now().plus(5, ChronoUnit.HOURS));
        Market market = newMarket(MARKET_ID, date, CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, addHours(date, -1), PROPS, BET_ID);
    }

    protected void createMarketWithSingleSettledBet() {
        Date now = Date.from(Instant.now());
        Market market = newMarket(MARKET_ID, now, CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        Date actionDate = Date.from(Instant.now().minus(1, ChronoUnit.HOURS));
        BetAction action = createAndSaveBetAction(market, actionDate, PROPS, BET_ID);
        SettledBet bet = new SettledBet(action.getSelectionId(), "x", 5, now, action.getPrice());
        bet.setBetAction(action);
        settledBetDao.saveOrUpdate(bet);
    }

    protected void createMarketWithPricesAndBets() {
        Date current = new Date();
        Market market = newMarket();
        MarketPrices prices = new MarketPrices(1, market, createRPs(2.1d, 2.2d));
        prices.setTime(DateUtils.addHours(current, -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(prices);
        betActionDao.saveOrUpdate(createAction(market, prices, "1"));
        MarketPrices prices2 = new MarketPrices(1, market, createRPs(2.3d, 2.5d));
        prices2.setTime(current);
        marketPricesDao.saveOrUpdate(prices2);
        betActionDao.saveOrUpdate(createAction(market, prices2, "2"));
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
    }

    protected void checkStats(long memoryCount, long hitCount, long missCount, long putCount, Class<?> clazz) {
        SessionFactory sessionFactory = getSessionFactory();
        SecondLevelCacheStatistics secondLevelCacheStatistics =
                sessionFactory.getStatistics().getSecondLevelCacheStatistics(clazz.getName());
        assertThat(secondLevelCacheStatistics.getElementCountInMemory(), Is.is(memoryCount));
        assertThat(secondLevelCacheStatistics.getHitCount(), Is.is(hitCount));
        assertThat(secondLevelCacheStatistics.getMissCount(), Is.is(missCount));
        assertThat(secondLevelCacheStatistics.getPutCount(), Is.is(putCount));
    }

    private BetAction createAction(Market market, MarketPrices prices, String betId) {
        BetAction place = new BetAction(BetActionType.PLACE, new Date(), new Price(2.2d, 2.1d, Side.LAY),
                market, CoreTestFactory.HOME, betId);
        place.setMarketPrices(prices);
        return place;
    }

    private List<RunnerPrices> createRPs(double price, double lastMatchedPrice) {
        return Lists.newArrayList(
                CoreTestFactory.newBackRP(price, CoreTestFactory.HOME, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.DRAW, lastMatchedPrice),
                CoreTestFactory.newBackRP(price, CoreTestFactory.AWAY, lastMatchedPrice));
    }

}
