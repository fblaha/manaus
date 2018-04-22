package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.util.Precision;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        Market market = newMarket();
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(1));
    }

    @Test(expected = LazyInitializationException.class)
    public void testMarketMarketFetch() {
        Market market = newMarket();
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(0).getMarket().getName();
    }

    @Test
    public void testMarketPrices2() {
        Market market = newMarket();
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(), new Date());
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        MarketPrices marketPrices2 = new MarketPrices(1, market, List.of(), new Date());
        marketPricesDao.saveOrUpdate(marketPrices2);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
    }

    @Test
    public void testMarketPricesOrder() {
        createBet();
        List<MarketPrices> shortList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID, OptionalInt.of(1));
        List<MarketPrices> marketPricesList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID);
        assertThat(marketPricesList.size(), is(2));
        MarketPrices longFirst = marketPricesList.get(0);
        assertTrue(longFirst.getTime().after(marketPricesList.get(1).getTime()));
        MarketPrices shortFirst = shortList.get(0);
        assertThat(shortFirst.getTime(), equalTo(longFirst.getTime()));
        assertThat(shortFirst.getRunnerPrices().stream().findFirst().get(),
                is(longFirst.getRunnerPrices().stream().findFirst().get()));
    }

    @Test
    public void testRunnerPricesDelete() {
        Market market = newMarket();
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(
                new RunnerPrices(232, List.of(new Price(2.3d, 22, Side.BACK)), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        marketDao.delete(market.getId());
    }

    @Test
    public void testRunnerPricesSort() {
        Market market = newMarket();
        Price better = new Price(2.3d, 22, Side.BACK);
        Price worse = new Price(2.2d, CoreTestFactory.DRAW, Side.BACK);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(
                new RunnerPrices(232, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(232).getPricesSorted()
                        .stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(232).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort2() {
        Market market = newMarket();
        Price better = new Price(2.2d, 22, Side.BACK);
        Price worse = new Price(2.2d, 22, Side.LAY);
        int selId = 232;
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(
                new RunnerPrices(selId, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort3() {
        Market market = newMarket();
        Price better = new Price(2.3d, 22, Side.LAY);
        Price worse = new Price(2.4d, CoreTestFactory.DRAW, Side.LAY);
        int selId = 232;
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(
                new RunnerPrices(selId, List.of(better, worse), 5d, 2.5d)), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> marketPricesList = marketPricesDao.getPrices(market.getId());
        assertThat(better,
                is(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(worse, is(getLast(marketPricesList.get(0).getRunnerPrices(selId).getPricesSorted())));
    }

    @Test
    public void testRunnerPricesSort4() {
        Market market = newMarket();
        Price layBetter = new Price(2.3d, 22, Side.LAY);
        Price layWorse = new Price(2.4d, CoreTestFactory.DRAW, Side.LAY);
        Price backBetter = new Price(2.3d, 22, Side.BACK);
        Price backWorse = new Price(2.2d, CoreTestFactory.DRAW, Side.BACK);
        int selId = 232;
        MarketPrices prices = new MarketPrices(1, market, List.of(
                new RunnerPrices(selId, List.of(layWorse, backWorse, backBetter, layBetter), 5d, 2.5d)), new Date());
        prices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(prices);
        List<MarketPrices> marketPricesList = marketPricesDao.getPrices(market.getId());
        MarketPrices marketPrices = marketPricesList.get(0);
        assertThat(4, is(marketPrices.getRunnerPrices(selId).getPricesSorted().size()));
        assertThat(backBetter,
                is(marketPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
        assertThat(layWorse, is(getLast(marketPrices.getRunnerPrices(selId).getPricesSorted())));
        {
            MarketPrices backPrices = marketPrices.getHomogeneous(Side.BACK);
            assertThat(2, is(backPrices.getRunnerPrices(selId).getPricesSorted().size()));
            assertThat(backBetter,
                    is(backPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
            assertThat(backWorse, is(getLast(backPrices.getRunnerPrices(selId).getPricesSorted())));
        }
        {
            MarketPrices layPrices = marketPrices.getHomogeneous(Side.LAY);
            assertThat(2, is(layPrices.getRunnerPrices(selId).getPricesSorted().size()));
            assertThat(layBetter, is(layPrices.getRunnerPrices(selId).getPricesSorted().stream().findFirst().get()));
            assertThat(layWorse, is(getLast(layPrices.getRunnerPrices(selId).getPricesSorted())));
        }
    }

    @Test
    public void testMarketPriceOrder2() {
        Market market = newMarket();
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(), new Date());
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        MarketPrices marketPrices2 = new MarketPrices(1, market, List.of(), new Date());
        marketPrices2.setTime(DateUtils.addMonths(new Date(), -2));
        marketPricesDao.saveOrUpdate(marketPrices2);
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size(), is(2));
        assertTrue(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(0).getTime().getTime() > marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).get(1).getTime().getTime());
    }

    @Test
    public void testMarketDelete() {
        Market market = newMarket("55", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        MarketPrices marketPrices2 = new MarketPrices(1, market, List.of(), new Date());
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
        for (Long selectionId : List.of(CoreTestFactory.HOME, CoreTestFactory.DRAW, CoreTestFactory.AWAY)) {
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
        }
    }

    @Test
    public void testReciprocalBack() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        MarketPrices marketPrices = getPrices(market, 3d);
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    private MarketPrices getPrices(Market market, double bestPrice) {
        RunnerPrices home = new RunnerPrices(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(bestPrice, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices draw = new RunnerPrices(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(bestPrice, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices away = new RunnerPrices(22, List.of(
                new Price(bestPrice, 100d, Side.BACK),
                new Price(2d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        MarketPrices result = new MarketPrices(1, market, List.of(home, draw, away), new Date());
        result.setTime(new Date());
        return result;
    }

    @Test
    public void testReciprocalTimeSort() {
        Date currDate = new Date();
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        savePrices(currDate, market, 3d);
        savePrices(DateUtils.addDays(currDate, -1), market, 2.7d);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    @Test
    public void testReciprocalTimeSort2() {
        Date currDate = new Date();
        Market market = newMarket();
        marketDao.saveOrUpdate(market);

        savePrices(DateUtils.addDays(currDate, -1), market, 2.7d);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(Precision.round(getMarketReciprocal(latest, Side.BACK).getAsDouble(), 6), is(0.9d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());

        savePrices(currDate, market, 3d);
        latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }

    private void savePrices(Date currDate, Market market, double bestPrice) {
        MarketPrices marketPrices = getPrices(market, bestPrice);
        marketPrices.setTime(currDate);
        marketPricesDao.saveOrUpdate(marketPrices);
    }

    @Test
    public void testReciprocalBack2() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        RunnerPrices home = new RunnerPrices(22, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(4d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices draw = new RunnerPrices(33, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(4d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices away = new RunnerPrices(44, List.of(
                new Price(2d, 100d, Side.BACK),
                new Price(2d, 100d, Side.BACK),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(1d));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
    }


    @Test
    public void testReciprocalLay() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        RunnerPrices home = new RunnerPrices(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices draw = new RunnerPrices(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices away = new RunnerPrices(22, List.of(
                new Price(2d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertThat(getMarketReciprocal(latest, Side.LAY).getAsDouble(), is(1d));
        assertThat(getMarketReciprocal(latest, Side.BACK).getAsDouble(), is(0.5d));
    }

    @Test
    public void testReciprocalNull() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        RunnerPrices home = new RunnerPrices(22, List.of(
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices draw = new RunnerPrices(22, List.of(
                new Price(4d, 100d, Side.LAY),
                new Price(1.5d, 100d, Side.BACK)), 2d, 2d);
        RunnerPrices away = new RunnerPrices(22, List.of(
                new Price(1.5d, 100d, Side.LAY)), 2d, 2d);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(home, draw, away), new Date());
        marketPricesDao.saveOrUpdate(marketPrices);
        List<MarketPrices> latest = marketPricesDao.getPrices(market.getId(), OptionalInt.of(1));
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent());
        assertFalse(getMarketReciprocal(latest, Side.BACK).isPresent());
    }

    private OptionalDouble getMarketReciprocal(List<MarketPrices> latestPrices, Side side) {
        Optional<MarketPrices> latest = latestPrices.stream().findFirst();
        return latest.map(marketPrices -> marketPrices.getReciprocal(side))
                .orElse(OptionalDouble.empty());
    }

}