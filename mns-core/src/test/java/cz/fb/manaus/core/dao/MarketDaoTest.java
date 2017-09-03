package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Event;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static com.google.common.collect.Iterables.get;
import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static java.util.Date.from;
import static java.util.Optional.of;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MarketDaoTest extends AbstractDaoTest {

    @Test
    public void testMarketGet() {
        Date curr = new Date();
        marketDao.saveOrUpdate(newMarket(CoreTestFactory.MARKET_ID, curr, CoreTestFactory.MATCH_ODDS));
        assertThat(marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(of(addHours(curr, -1)), Optional.empty(), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(of(addHours(curr, -1)), of(addHours(curr, 1)), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(of(curr), of(curr), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(of(addHours(curr, 1)), Optional.empty(), OptionalInt.empty()).size(), is(0));

        assertThat(marketDao.getMarkets(Optional.empty(), of(addHours(curr, 1)), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(Optional.empty(), of(curr), OptionalInt.empty()).size(), is(1));
        assertThat(marketDao.getMarkets(Optional.empty(), of(addHours(curr, -1)), OptionalInt.empty()).size(), is(0));
    }

    @Test
    public void testMarketOrder() {
        Date date = DateUtils.truncate(new Date(), Calendar.MONTH);
        marketDao.saveOrUpdate(newMarket("33", addHours(date, 2), CoreTestFactory.MATCH_ODDS));
        marketDao.saveOrUpdate(newMarket("22", addHours(date, 2), CoreTestFactory.MATCH_ODDS));
        marketDao.saveOrUpdate(newMarket("44", addHours(date, 1), CoreTestFactory.MATCH_ODDS));
        List<Market> markets = marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty());
        assertThat(markets.get(0).getId(), is("44"));
        assertThat(markets.get(1).getId(), is("22"));
        assertThat(markets.get(2).getId(), is("33"));
    }

    @Test
    public void testMarketSave() {
        marketDao.saveOrUpdate(newMarket());
    }

    @Test
    public void testRunner() {
        marketDao.saveOrUpdate(newMarket());
        Market market = marketDao.get(CoreTestFactory.MARKET_ID).get();
        assertThat(market.getRunners().size(), is(3));
        assertThat(get(market.getRunners(), 0).getName(), is(CoreTestFactory.HOME_NAME));
        assertThat(get(market.getRunners(), 1).getName(), is(CoreTestFactory.DRAW_NAME));
        assertThat(get(market.getRunners(), 2).getName(), is(CoreTestFactory.AWAY_NAME));
    }

    @Test
    public void testMarketMerge() {
        Market market = newMarket();
        Event childA = new Event("55", "childA", new Date(), CoreTestFactory.COUNTRY_CODE);
        market.setEvent(childA);
        marketDao.saveOrUpdate(market);
        Market toBeMerged = newMarket(CoreTestFactory.MARKET_ID, new Date(), SPARTA);
        Event childB = new Event("55", "childB", new Date(), CoreTestFactory.COUNTRY_CODE);
        toBeMerged.setEvent(childB);
        marketDao.saveOrUpdate(toBeMerged);
        market = marketDao.get(CoreTestFactory.MARKET_ID).get();
        assertThat(market.getEvent().getId(), is("55"));
        assertThat(market.getEvent().getName(), is("childB"));
        assertThat(market.getName(), is(SPARTA));
        assertThat(market.getEvent().getCountryCode(), is(CoreTestFactory.COUNTRY_CODE));
    }

    @Test
    public void testMarketSaveSubsequentUpdate() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getRunners().size(), is(3));
        market = newMarket(CoreTestFactory.MARKET_ID, new Date(), "new name");
        marketDao.saveOrUpdate(market);
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getName(), is("new name"));
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getEvent().getCountryCode(), is(CoreTestFactory.COUNTRY_CODE));
    }

    @Test
    public void testMarketVersion() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getVersion(), is(0));
        market = newMarket(CoreTestFactory.MARKET_ID, new Date(), SPARTA);
        marketDao.saveOrUpdate(market);
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getVersion(), is(1));
        market = newMarket(CoreTestFactory.MARKET_ID, new Date(), "yet another");
        marketDao.saveOrUpdate(market);
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().getVersion(), is(2));
    }

    @Test
    public void testMarketBulkDelete() {
        createBet();
        int count = marketDao.deleteMarkets(from(Instant.now().minus(1, ChronoUnit.HOURS)));
        assertThat(count, is(0));
        count = marketDao.deleteMarkets(from(Instant.now().plus(3, ChronoUnit.HOURS)));
        assertThat(count, is(1));
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(0));
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).orElse(null), nullValue());
    }

}