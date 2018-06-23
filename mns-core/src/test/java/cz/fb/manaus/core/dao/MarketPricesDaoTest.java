package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.MarketPricesTest;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.RunnerPricesTest;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.util.Precision;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static com.google.common.collect.Iterables.getLast;
import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MarketPricesDaoTest extends AbstractDaoTest {

    @Test
    public void testMarketPrices() {
        var market = newMarket();
        var marketPrices = MarketPricesTest.create(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(1));
    }

    @Test(expected = LazyInitializationException.class)
    public void testMarketMarketFetch() {
        var market = newMarket();
        var marketPrices = MarketPricesTest.create(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(0).getMarket().getName();
    }

    @Test
    public void testMarketPrices2() {
        var market = newMarket();
        var marketPrices = MarketPricesTest.create(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPrices2 = MarketPricesTest.create(1, market, List.of(), new Date());
        marketPricesDao.saveOrUpdate(marketPrices2);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
    }

    @Test
    public void testMarketPricesOrder() {
        createBet();
        var shortList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID, OptionalInt.of(1));
        var marketPricesList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID);
        assertThat(marketPricesList.size(), is(2));
        var longFirst = marketPricesList.get(0);
        assertTrue(longFirst.getTime().after(marketPricesList.get(1).getTime()));
        var shortFirst = shortList.get(0);
        assertThat(shortFirst.getTime(), equalTo(longFirst.getTime()));
        assertThat(shortFirst.getRunnerPrices().stream().findFirst().get(),
                is(longFirst.getRunnerPrices().stream().findFirst().get()));
    }

    @Test
    public void testRunnerPricesDelete() {
        var market = newMarket();
        var marketPrices = MarketPricesTest.create(1, market, List.of(
                RunnerPricesTest.create(232, List.of(new Price(2.3d, 22, Side.BACK)), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        marketDao.delete(market.getId());
    }

    @Test
    public void testRunnerPricesSort() {
        var market = newMarket();
        var better = new Price(2.3d, 22, Side.BACK);
        var worse = new Price(2.2d, CoreTestFactory.DRAW, Side.BACK);
        var marketPrices = MarketPricesTest.create(1, market, List.of(
                RunnerPricesTest.create(232, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(232).getPricesSorted()
                        .stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(232).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort2() {
        var market = newMarket();
        var better = new Price(2.2d, 22, Side.BACK);
        var worse = new Price(2.2d, 22, Side.LAY);
        var selId = 232;
        var marketPrices = MarketPricesTest.create(1, market, List.of(
                RunnerPricesTest.create(selId, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort3() {
        var market = newMarket();
        var better = new Price(2.3d, 22, Side.LAY);
        var worse = new Price(2.4d, CoreTestFactory.DRAW, Side.LAY);
        var selId = 232;
        var marketPrices = MarketPricesTest.create(1, market, List.of(
                RunnerPricesTest.create(selId, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort4() {
        var market = newMarket();
        var layBetter = new Price(2.3d, 22, Side.LAY);
        var layWorse = new Price(2.4d, CoreTestFactory.DRAW, Side.LAY);
        var backBetter = new Price(2.3d, 22, Side.BACK);
        var backWorse = new Price(2.2d, CoreTestFactory.DRAW, Side.BACK);
        var selId = 232;
        var prices = MarketPricesTest.create(1, market, List.of(
                RunnerPricesTest.create(selId, List.of(layWorse, backWorse, backBetter, layBetter), 5d, 2.5d)), new Date());
        prices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(prices);
        var marketPricesList = marketPricesDao.getPrices(market.getId());
        MarketPrices marketPrices = marketPricesList.get(0);
        assertThat(4, is(marketPrices.getRunnerPrices(selId).getPricesSorted().size()));
        assertThat(backBetter,
                is(marketPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(layWorse, is(getLast(marketPrices.getRunnerPrices(selId).getPricesSorted())));
        {
            var backPrices = marketPrices.getHomogeneous(Side.BACK);
            assertThat(2, is(backPrices.getRunnerPrices(selId).getPricesSorted().size()));
            assertThat(backBetter,
                    is(backPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
            assertThat(backWorse, is(getLast(backPrices.getRunnerPrices(selId).getPricesSorted())));
        }
        {
            var layPrices = marketPrices.getHomogeneous(Side.LAY);
            assertThat(2, is(layPrices.getRunnerPrices(selId).getPricesSorted().size()));
            assertThat(layBetter, is(layPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
            assertThat(layWorse, is(getLast(layPrices.getRunnerPrices(selId).getPricesSorted())));
        }
    }

    @Test
    public void testMarketPriceOrder2() {
        var market = newMarket();
        var marketPrices = MarketPricesTest.create(1, market, List.of(), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPrices2 = MarketPricesTest.create(1, market, List.of(), new Date());
        marketPrices2.setTime(DateUtils.addMonths(new Date(), -2));
        marketPricesDao.saveOrUpdate(marketPrices2);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
        assertTrue(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(0).getTime().getTime() > marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(1).getTime().getTime());
    }

    @Test
    public void testMarketDelete() {
        var market = newMarket("55", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        var marketPrices = MarketPricesTest.create(1, market, List.of(), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        var marketPrices2 = MarketPricesTest.create(1, market, List.of(), new Date());
        marketPricesDao.saveOrUpdate(marketPrices2);
        assertThat(marketPricesDao.getPrices("55").size(), is(2));
        marketDao.delete("55");
        assertThat(marketPricesDao.getPrices("55").size(), is(0));
    }

    @Test
    public void testMarketDelete2() {
        createBet();
        marketDao.delete(CoreTestFactory.MARKET_ID);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(0));
    }

    @Test
    public void testRunnerPricesByMarketAndSelection() {
        createBet();
        List.of(CoreTestFactory.HOME, CoreTestFactory.DRAW, CoreTestFactory.AWAY).forEach(selectionId -> {
            List<RunnerPrices> shortList = marketPricesDao.getRunnerPrices(CoreTestFactory.MARKET_ID, selectionId, OptionalInt.of(1));
            List<RunnerPrices> complete = marketPricesDao.getRunnerPrices(CoreTestFactory.MARKET_ID, selectionId, OptionalInt.empty());
            assertThat(complete.size(), is(2));
            assertThat(complete.get(0).getSelectionId(), is(selectionId));
            assertThat(complete.get(0).getBestPrice().get().getPrice(), is(2.3d));
            assertThat(complete.get(1).getSelectionId(), is(selectionId));
            assertThat(complete.get(1).getBestPrice().get().getPrice(), is(2.1d));
            assertThat(shortList.size(), is(1));
            assertThat(shortList.get(0).getSelectionId(), is(selectionId));
            assertThat(shortList.get(0).getBestPrice().get().getPrice(), is(2.3d));
        });
    }

    @Test
    public void testReciprocalBack() {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var marketPrices = getPrices(market, 3d);
        marketPricesDao.saveOrUpdate(marketPrices);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    private MarketPrices getPrices(Market market, double bestPrice) {
        var home = RunnerPricesTest.create(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(bestPrice, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var draw = RunnerPricesTest.create(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(bestPrice, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var away = RunnerPricesTest.create(22, List.of(
                new Price(bestPrice, 100d, Side.BACK),
                new Price(2d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var result = MarketPricesTest.create(1, market, List.of(home, draw, away), new Date());
        result.setTime(new Date());
        return result;
    }

    @Test
    public void testReciprocalTimeSort() {
        var currDate = new Date();
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        savePrices(currDate, market, 3d);
        savePrices(DateUtils.addDays(currDate, -1), market, 2.7d);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    @Test
    public void testReciprocalTimeSort2() {
        var currDate = new Date();
        var market = newMarket();
        marketDao.saveOrUpdate(market);

        savePrices(DateUtils.addDays(currDate, -1), market, 2.7d);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(Precision.round(getMarketReciprocal(latest, Side.BACK).getAsDouble(), 6), is(0.9d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());

        savePrices(currDate, market, 3d);
        latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    private void savePrices(Date currDate, Market market, double bestPrice) {
        var marketPrices = getPrices(market, bestPrice);
        marketPrices.setTime(currDate);
        marketPricesDao.saveOrUpdate(marketPrices);
    }

    @Test
    public void testReciprocalBack2() {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var home = RunnerPricesTest.create(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(4d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var draw = RunnerPricesTest.create(33, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(4d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var away = RunnerPricesTest.create(44, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(2d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var marketPrices = MarketPricesTest.create(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }


    @Test
    public void testReciprocalLay() {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var home = RunnerPricesTest.create(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var draw = RunnerPricesTest.create(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var away = RunnerPricesTest.create(22, List.of(
                new Price(2d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var marketPrices = MarketPricesTest.create(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.LAY).getAsDouble(), is(1d));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(0.5d));
    }

    @Test
    public void testReciprocalNull() {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var home = RunnerPricesTest.create(22, List.of(
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var draw = RunnerPricesTest.create(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        var away = RunnerPricesTest.create(22, List.of(
                new Price(1.5d, 100d, Side.LAY)), 2d, 2d);
        var marketPrices = MarketPricesTest.create(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        var latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
        assertFalse(getMarketReciprocal(latest, Side.BACK).isPresent());
    }

    private OptionalDouble getMarketReciprocal(List<MarketPrices> latestPrices, Side side) {
        var latest = latestPrices.stream().findFirst();
        return latest.map(marketPrices -> marketPrices.getReciprocal(side))
                .orElse(OptionalDouble.empty());
    }

}