package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBetTest;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;

import java.util.Date;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SettledBetDaoTest extends AbstractDaoTest {


    public static final String MARKET_ID = "33";

    @Test
    public void testSettledBet() {
        var curr = new Date();
        var market = newMarket(MARKET_ID, curr, CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        var fooAction = createAndSaveBetAction(market, curr, PROPS, "foo");
        var settledBet = SettledBetTest.create(555, CoreTestFactory.DRAW_NAME, 5d,
                curr, new Price(3d, 5d, Side.LAY));
        settledBet.setBetAction(fooAction);
        settledBetDao.saveOrUpdate(settledBet);
        assertThat(settledBetDao.getSettledBets(of(addHours(curr, 1)), of(new Date()), empty(), OptionalInt.empty()).size(), is(0));
        assertThat(settledBetDao.getSettledBets(of(addHours(curr, -1)), of(new Date()), empty(), OptionalInt.empty()).size(), is(1));

        var bets = settledBetDao.getSettledBets(of(addHours(curr, -1)), of(new Date()), empty(), OptionalInt.empty());
        assertThat(bets.size(), is(1));
        assertThat(bets.get(0).getBetAction().getMarket().getId(), is(MARKET_ID));
        assertThat(bets.get(0).getSelectionName(), is(CoreTestFactory.DRAW_NAME));
        assertThat(settledBetDao.getSettledBets(empty(), empty(), empty(), OptionalInt.empty()).size(), is(1));

        assertThat(settledBetDao.getSettledBets("34", OptionalLong.empty(), empty()).size(), is(0));
        bets = settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), empty());
        assertThat(bets.size(), is(1));
        assertThat(bets.get(0).getBetAction().getMarket().getId(), is(MARKET_ID));
        assertThat(bets.get(0).getSelectionName(), is(CoreTestFactory.DRAW_NAME));

        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), empty()).size(), is(1));
        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), empty()).size(), is(1));
        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), of(Side.LAY)).size(), is(1));
        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.empty(), of(Side.LAY)).size(), is(1));

        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(556L), empty()).size(), is(0));
        assertThat(settledBetDao.getSettledBets(MARKET_ID, OptionalLong.of(555L), of(Side.BACK)).size(), is(0));

    }

    @Test
    public void testSettledBetByBetId() {
        storeMarketAndBet();
        assertThat(settledBetDao.getSettledBet(BET_ID).orElse(null), notNullValue());
        assertThat(settledBetDao.getSettledBet(BET_ID + 1).orElse(null), nullValue());
    }

    @Test
    public void testRunnerCountSingle() {
        storeMarketAndBet();
        var stored = settledBetDao.getSettledBet(BET_ID);
        assertThat(stored.get().getBetAction().getMarket().getRunners().size(), is(3));
    }

    @Test
    public void testRunnerCountBatch() {
        storeMarketAndBet();
        var settledBets = settledBetDao.getSettledBets(of(addDays(new Date(), -5)), of(new Date()),
                empty(), OptionalInt.empty());
        var stored = settledBets.get(0);
        assertThat(stored.getBetAction().getMarket().getRunners().size(), is(3));
    }

    @Test
    public void testMaxResults() {
        var market = newMarket(MARKET_ID, new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        var fooAction = createAndSaveBetAction(market, new Date(), PROPS, "foo");
        var barAction = createAndSaveBetAction(market, new Date(), PROPS, "bar");
        var fooBet = SettledBetTest.create(555, CoreTestFactory.DRAW_NAME, 5d,
                new Date(), new Price(3d, 5d, Side.LAY));
        fooBet.setBetAction(fooAction);
        var barBet = SettledBetTest.create(556, CoreTestFactory.DRAW_NAME, 5d,
                new Date(), new Price(3d, 5d, Side.LAY));
        barBet.setBetAction(barAction);
        settledBetDao.saveOrUpdate(fooBet);
        settledBetDao.saveOrUpdate(barBet);
        assertThat(settledBetDao.getSettledBets(of(addDays(new Date(), -5)), of(new Date()), empty(), OptionalInt.of(1)).size(), is(1));
        assertThat(settledBetDao.getSettledBets(of(addDays(new Date(), -5)), of(new Date()), empty(), OptionalInt.of(2)).size(), is(2));
    }


    private void storeMarketAndBet() {
        var market = newMarket(MARKET_ID, new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        var fooAction = createAndSaveBetAction(market, new Date(), PROPS, BET_ID);
        var settledBet = SettledBetTest.create(555, CoreTestFactory.DRAW_NAME, 5d,
                new Date(), new Price(3d, 5d, Side.LAY));
        settledBet.setBetAction(fooAction);
        settledBetDao.saveOrUpdate(settledBet);
    }

}