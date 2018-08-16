package cz.fb.manaus.reactor.price;

import com.google.common.primitives.Doubles;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.model.factory.MarketPricesFactory;
import cz.fb.manaus.core.model.factory.RunnerPricesFactory;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PriceServiceTest extends AbstractLocalTestCase {

    @Autowired
    private PriceService priceService;
    @Autowired
    private ReactorTestFactory factory;
    @Autowired
    private ExchangeProvider provider;
    @Autowired
    private FairnessPolynomialCalculator calculator;

    @Test
    public void testDowngradePrice() {
        assertEquals(3d, priceService.downgrade(3d, 0d, Side.BACK), 0.000001);
        assertEquals(3d, priceService.downgrade(3d, 0d, Side.LAY), 0.000001);
        assertEquals(1d, priceService.downgrade(3d, 1d, Side.LAY), 0.000001);
        assertEquals(2d, priceService.downgrade(3d, 0.5, Side.LAY), 0.000001);
        assertEquals(5d, priceService.downgrade(3d, 0.5, Side.BACK), 0.000001);
        assertEquals(3.88d, priceService.downgrade(4d, 0.04, Side.LAY), 0.000001);
        assertEquals(4.125d, priceService.downgrade(4d, 0.04, Side.BACK), 0.000001);
    }

    @Test
    public void testDowngrade() {
        checkDownGrade(2d, 3d, Side.LAY);
        checkDownGrade(3d, 2d, Side.BACK);
    }

    private void checkDownGrade(double newPrice, double oldPrice, Side type) {
        var newOne = new Price(newPrice, 10d, type);
        var oldOne = new Price(oldPrice, 10d, type);
        assertThat(isDowngrade(newOne, oldOne), is(true));
        assertThat(isDowngrade(oldOne, newOne), is(false));
    }

    private boolean isDowngrade(Price newOne, Price oldOne) {
        var type = Objects.requireNonNull(newOne.getSide());
        var newPrice = newOne.getPrice();
        var oldPrice = oldOne.getPrice();
        return priceService.isDowngrade(newPrice, oldPrice, type);
    }

    private double getFairness(Side side, MarketPrices marketPrices) {
        return calculator.getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(side)).getAsDouble();
    }

    @Test
    public void testFairPrice() {
        var marketPrices = MarketPricesFactory.create(1, null, List.of(factory.newRP(1, 4.2, 6), factory.newRP(2, 2.87, 4), factory.newRP(1, 1.8, 3)), new Date());
        var layFairness = getFairness(Side.LAY, marketPrices);
        assertEquals(1.5d, layFairness, 0.1d);
        var backFairness = getFairness(Side.BACK, marketPrices);
        assertEquals(0.8d, backFairness, MarketPrices.FAIR_EPS);

        assertEquals(5d, priceService.getRoundedFairnessFairPrice(4.2, backFairness).getAsDouble(), 0.01);
        assertEquals(3.35d, priceService.getRoundedFairnessFairPrice(2.87, backFairness).getAsDouble(), 0.01);
        assertEquals(2d, priceService.getRoundedFairnessFairPrice(1.8, backFairness).getAsDouble(), 0.01);
    }

    @Test
    public void testFairPriceOverroundThree() {
        checkFairPrices(1, 2.5, 3.25, 3);
        checkFairPrices(1, 2.7, 2.7, 2.7);
    }

    @Test
    public void testFairPriceOverroundTwo() {
        checkFairPrices(1, 1.3, 1.8);
        checkFairPrices(1, 1.3, 2.2);
        checkFairPrices(1, 1.1, 2.7);
        checkFairPrices(1, 1.03, 15);
        checkFairPrices(1, 1.03, 1.04);
    }

    @Test
    public void testFairPriceOverroundRealFootball() {
        checkFairPrices(1, 1.44, 4.1, 6.4);
        checkFairPrices(1, 1.1, 8, 15);
    }

    @Test
    public void testFairPriceOverroundTwoGenerated() {
        rangeClosed(1, 9).forEach(i -> {
            checkFairPrices(1, 1.9, 1d + i * 0.1d);
        });
    }

    @Test
    public void testFairPriceOverroundThreeGenerated() {
        rangeClosed(1, 19).forEach(i -> {
            var price = 1d + i * 0.1d;
            checkFairPrices(1, 2.8, 2.8, price);
        });
        rangeClosed(1, 19).forEach(i -> {
            var price = 1d + i * 0.1d;
            checkFairPrices(1, 2.8, 1.5, price);
        });
    }


    @Test
    public void testFairPriceOverroundRealBasketball() {
        checkFairPrices(1, 1.34, 17, 3.24);
    }

    @Test
    public void testFairPriceOverroundRealTennis() {
        checkFairPrices(1, 1.73, 1.88);
        checkFairPrices(1, 1.09, 1.59);
        checkFairPrices(1, 1.4, 2.6);
    }

    @Test
    public void testFairPriceTwoWinnerCount() {
        checkFairPrices(2, 1.4, 1.4, 1.4);
    }

    private void checkFairPrices(int winnerCount, double... unfairPrices) {
        var marketPrices = MarketPricesFactory.create(winnerCount, null, factory.createRP(Doubles.asList(unfairPrices)), new Date());
        var overround = marketPrices.getOverround(Side.BACK);
        var reciprocal = marketPrices.getReciprocal(Side.BACK).getAsDouble();
        var fairness = getFairness(Side.BACK, marketPrices);
        assertTrue(overround.getAsDouble() > 1);

        var overroundPrices = DoubleStream.of(unfairPrices)
                .map(price -> {
                    var fair = priceService.getOverroundFairPrice(price, overround.getAsDouble(),
                            winnerCount, unfairPrices.length);
                    assertTrue(price < fair);
                    return fair;
                })
                .boxed().collect(Collectors.toList());

        checkOverroundUnfairPrices(reciprocal, winnerCount, Doubles.asList(unfairPrices), overroundPrices);

        var fairnessPrices = DoubleStream.of(unfairPrices)
                .map(price -> {
                    var fair = priceService.getFairnessFairPrice(price, fairness);
                    assertTrue(price < fair);
                    return fair;
                })
                .boxed().collect(Collectors.toList());

        var overFairnessBased = MarketPricesFactory.create(winnerCount, null, factory.createRP(fairnessPrices), new Date()).getOverround(Side.BACK);
        var overOverroundBased = MarketPricesFactory.create(winnerCount, null, factory.createRP(overroundPrices), new Date()).getOverround(Side.BACK);

        assertEquals((double) winnerCount, overFairnessBased.getAsDouble(), 0.001);
        assertEquals((double) winnerCount, overOverroundBased.getAsDouble(), 0.001);
    }

    private void checkOverroundUnfairPrices(double reciprocal, int winnerCount, List<Double> unfairPrices, List<Double> fairPrices) {
        for (var i = 0; i < unfairPrices.size(); i++) {
            double originalUnfairPrice = unfairPrices.get(i);
            double fairPrice = fairPrices.get(i);
            var unfairPrice = getOverroundUnfairPrice(fairPrice, reciprocal, winnerCount, unfairPrices.size());
            assertEquals(originalUnfairPrice, unfairPrice, 0.000001d);
        }
    }

    @Test
    public void testFairnessHighProbability() {
        var lowPrice = 1.04d;
        var highPrice = 15d;
        var home = RunnerPricesFactory.create(CoreTestFactory.HOME, List.of(new Price(lowPrice, 10d, Side.BACK)), 50d, lowPrice);
        var away = RunnerPricesFactory.create(CoreTestFactory.AWAY, List.of(new Price(highPrice, 10d, Side.BACK)), 50d, highPrice);
        var marketPrices = MarketPricesFactory.create(1, null, List.of(home, away), new Date());
        var fairness = getFairness(Side.BACK, marketPrices);
        var lowFairPrice = priceService.getFairnessFairPrice(lowPrice, fairness);
        var highFairPrice = priceService.getFairnessFairPrice(highPrice, fairness);
        assertTrue(highPrice < highFairPrice);
        assertTrue(lowPrice < lowFairPrice);
    }

    @Test
    public void testFairPrices() {
        var market = factory.createMarket(0.2, List.of(0.85d, 0.1d, 0.05d));
        var fairness = calculator.getFairness(market);
        var bestBack = market.getBestPrices(Side.BACK).get(0).getAsDouble();
        var bestLay = market.getBestPrices(Side.LAY).get(0).getAsDouble();
        var fairnessBackFairPrice = priceService.getFairnessFairPrice(bestBack, fairness.get(Side.BACK).getAsDouble());
        var fairnessLayFairPrice = priceService.getFairnessFairPrice(bestLay, fairness.get(Side.LAY).getAsDouble());
        assertEquals(fairnessBackFairPrice, fairnessLayFairPrice, 0.01d);
    }

    private double getOverroundUnfairPrice(double fairPrice, double targetReciprocal, int winnerCount, int runnerCount) {
        var overround = winnerCount / targetReciprocal;
        var selectionOverround = (overround - winnerCount) / runnerCount;
        var probability = 1 / fairPrice;
        return Math.max(1 / (selectionOverround + probability), provider.getMinPrice());
    }
}
