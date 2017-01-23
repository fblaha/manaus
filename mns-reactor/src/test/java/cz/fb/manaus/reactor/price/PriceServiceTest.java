package cz.fb.manaus.reactor.price;

import com.google.common.primitives.Doubles;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Collections.singletonList;
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
    public void testDowngradePrice() throws Exception {
        assertEquals(3d, priceService.downgrade(3d, 0d, Side.BACK), 0.000001);
        assertEquals(3d, priceService.downgrade(3d, 0d, Side.LAY), 0.000001);
        assertEquals(1d, priceService.downgrade(3d, 1d, Side.LAY), 0.000001);
        assertEquals(2d, priceService.downgrade(3d, 0.5, Side.LAY), 0.000001);
        assertEquals(5d, priceService.downgrade(3d, 0.5, Side.BACK), 0.000001);
        assertEquals(3.88d, priceService.downgrade(4d, 0.04, Side.LAY), 0.000001);
        assertEquals(4.125d, priceService.downgrade(4d, 0.04, Side.BACK), 0.000001);
    }

    @Test
    public void testDowngrade() throws Exception {
        checkDownGrade(2d, 3d, Side.LAY);
        checkDownGrade(3d, 2d, Side.BACK);
    }

    private void checkDownGrade(double newPrice, double oldPrice, Side type) {
        Price newOne = new Price(newPrice, 10d, type);
        Price oldOne = new Price(oldPrice, 10d, type);
        assertThat(isDowngrade(newOne, oldOne), is(true));
        assertThat(isDowngrade(oldOne, newOne), is(false));
    }

    private boolean isDowngrade(Price newOne, Price oldOne) {
        Side type = checkNotNull(newOne.getSide());
        double newPrice = newOne.getPrice();
        double oldPrice = oldOne.getPrice();
        return priceService.isDowngrade(newPrice, oldPrice, type);
    }

    private double getFairness(Side side, MarketPrices marketPrices) {
        return calculator.getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(side)).getAsDouble();
    }

    @Test
    public void testProgressiveAmount() throws Exception {
        assertThat(priceService.getProgressiveAmount(1.1, 1.2, 2.5, provider.getMinAmount()), is(7.2d));
        assertThat(priceService.getProgressiveAmount(1.2, 1.2, 2.5, provider.getMinAmount()), is(7.2d));
        assertThat(priceService.getProgressiveAmount(1.5, 1.2, 2.5, provider.getMinAmount()), is(3.6d));
        assertThat(priceService.getProgressiveAmount(2, 1.2, 2.5, provider.getMinAmount()), is(2.4d));
        assertThat(priceService.getProgressiveAmount(2.5, 1.2, 2.5, provider.getMinAmount()), is(provider.getMinAmount()));
        assertThat(priceService.getProgressiveAmount(3, 1.2, 2.5, provider.getMinAmount()), is(provider.getMinAmount()));
    }

    @Test
    public void testFairPrice() throws Exception {
        MarketPrices marketPrices = new MarketPrices(1, null,
                Arrays.asList(factory.newRP(1, 4.2, 6), factory.newRP(2, 2.87, 4), factory.newRP(1, 1.8, 3)));
        Double layFairness = getFairness(Side.LAY, marketPrices);
        assertEquals(1.5d, layFairness, 0.1d);
        double backFairness = getFairness(Side.BACK, marketPrices);
        assertEquals(0.8d, backFairness, MarketPrices.FAIR_EPS);

        assertEquals(5d, priceService.getRoundedFairnessFairPrice(4.2, backFairness).getAsDouble(), 0.01);
        assertEquals(3.35d, priceService.getRoundedFairnessFairPrice(2.87, backFairness).getAsDouble(), 0.01);
        assertEquals(2d, priceService.getRoundedFairnessFairPrice(1.8, backFairness).getAsDouble(), 0.01);
    }

    @Test
    public void testFairPriceOverroundThree() throws Exception {
        checkFairPrices(1, 2.5, 3.25, 3);
        checkFairPrices(1, 2.7, 2.7, 2.7);
    }

    @Test
    public void testFairPriceOverroundTwo() throws Exception {
        checkFairPrices(1, 1.3, 1.8);
        checkFairPrices(1, 1.3, 2.2);
        checkFairPrices(1, 1.1, 2.7);
        checkFairPrices(1, 1.03, 15);
        checkFairPrices(1, 1.03, 1.04);
    }

    @Test
    public void testFairPriceOverroundRealFootball() throws Exception {
        checkFairPrices(1, 1.44, 4.1, 6.4);
        checkFairPrices(1, 1.1, 8, 15);
    }

    @Test
    public void testFairPriceOverroundTwoGenerated() throws Exception {
        rangeClosed(1, 9).forEach(i -> {
            checkFairPrices(1, 1.9, 1d + i * 0.1d);
        });
    }

    @Test
    public void testFairPriceOverroundThreeGenerated() throws Exception {
        rangeClosed(1, 19).forEach(i -> {
            double price = 1d + i * 0.1d;
            checkFairPrices(1, 2.8, 2.8, price);
        });
        rangeClosed(1, 19).forEach(i -> {
            double price = 1d + i * 0.1d;
            checkFairPrices(1, 2.8, 1.5, price);
        });
    }


    @Test
    public void testFairPriceOverroundRealBasketball() throws Exception {
        checkFairPrices(1, 1.34, 17, 3.24);
    }

    @Test
    public void testFairPriceOverroundRealTennis() throws Exception {
        checkFairPrices(1, 1.73, 1.88);
        checkFairPrices(1, 1.09, 1.59);
        checkFairPrices(1, 1.4, 2.6);
    }

    @Test
    public void testFairPriceTwoWinnerCount() throws Exception {
        checkFairPrices(2, 1.4, 1.4, 1.4);
    }

    private void checkFairPrices(int winnerCount, double... unfairPrices) {
        System.out.println("unfairPrices = " + Doubles.asList(unfairPrices));
        MarketPrices marketPrices = new MarketPrices(winnerCount, null, factory.createRP(Doubles.asList(unfairPrices)));
        OptionalDouble overround = marketPrices.getOverround(Side.BACK);
        double reciprocal = marketPrices.getReciprocal(Side.BACK).getAsDouble();
        double fairness = getFairness(Side.BACK, marketPrices);
        assertTrue(overround.getAsDouble() > 1);

        List<Double> overroundPrices = DoubleStream.of(unfairPrices)
                .map(price -> {
                    double fair = priceService.getOverroundFairPrice(price, overround.getAsDouble(),
                            winnerCount, unfairPrices.length);
                    assertTrue(price < fair);
                    return fair;
                })
                .boxed().collect(Collectors.toList());

        checkOverroundUnfairPrices(reciprocal, winnerCount, Doubles.asList(unfairPrices), overroundPrices);
        List<Double> fairnessPrices = from(Doubles.asList(unfairPrices))
                .transform(price -> {
                    double fair = priceService.getFairnessFairPrice(price, fairness);
                    assertTrue(price < fair);
                    return fair;
                })
                .toList();
        List<Double> reciprocalPrices = from(Doubles.asList(unfairPrices))
                .transform(price -> {
                    double fair = priceService.getReciprocalFairPrice(price, reciprocal);
                    assertTrue(price < fair);
                    return fair;
                })
                .toList();
        System.out.println("reciprocalPrices = " + reciprocalPrices);
        System.out.println("fairnessPrices = " + fairnessPrices);
        System.out.println("overroundPrices = " + overroundPrices);


        OptionalDouble overReciprocalBased = new MarketPrices(winnerCount, null, factory.createRP(reciprocalPrices)).getOverround(Side.BACK);
        OptionalDouble overFairnessBased = new MarketPrices(winnerCount, null, factory.createRP(fairnessPrices)).getOverround(Side.BACK);
        OptionalDouble overOverroundBased = new MarketPrices(winnerCount, null, factory.createRP(overroundPrices)).getOverround(Side.BACK);
        System.out.println("overround = " + overround);
        System.out.println("overReciprocalBased = " + overReciprocalBased);
        System.out.println("overFairnessBased = " + overFairnessBased);
        System.out.println("overOverroundBased = " + overOverroundBased);

        assertEquals((double) winnerCount, overReciprocalBased.getAsDouble(), 0.001);
        assertEquals((double) winnerCount, overFairnessBased.getAsDouble(), 0.001);
        assertEquals((double) winnerCount, overOverroundBased.getAsDouble(), 0.001);
    }

    private void checkOverroundUnfairPrices(double reciprocal, int winnerCount, List<Double> unfairPrices, List<Double> fairPrices) {
        for (int i = 0; i < unfairPrices.size(); i++) {
            double originalUnfairPrice = unfairPrices.get(i);
            double fairPrice = fairPrices.get(i);
            double unfairPrice = getOverroundUnfairPrice(fairPrice, reciprocal, winnerCount, unfairPrices.size());
            assertEquals(originalUnfairPrice, unfairPrice, 0.000001d);
        }
    }


    @Test
    public void testFairnessHighProbability() throws Exception {
        double lowPrice = 1.04d, highPrice = 15d;
        RunnerPrices home = new RunnerPrices(CoreTestFactory.HOME, singletonList(new Price(lowPrice, 10d, Side.BACK)), 50d, lowPrice);
        RunnerPrices away = new RunnerPrices(CoreTestFactory.AWAY, singletonList(new Price(highPrice, 10d, Side.BACK)), 50d, highPrice);
        MarketPrices marketPrices = new MarketPrices(1, null, Arrays.asList(home, away));
        double fairness = getFairness(Side.BACK, marketPrices);
        double reciprocal = marketPrices.getReciprocal(Side.BACK).getAsDouble();
        double lowFairPrice = priceService.getFairnessFairPrice(lowPrice, fairness);
        double poorManLowFairPrice = priceService.getReciprocalFairPrice(lowPrice, reciprocal);
        double highFairPrice = priceService.getFairnessFairPrice(highPrice, fairness);
        double poorManHghFairPrice = priceService.getReciprocalFairPrice(highPrice, reciprocal);
        assertTrue(lowFairPrice < poorManLowFairPrice);
        assertTrue(highFairPrice > poorManHghFairPrice);
    }

    @Test
    public void testFairPrices() throws Exception {
        MarketPrices market = factory.createMarket(0.2, Arrays.asList(0.85d, 0.1d, 0.05d));
        OptionalDouble reciprocalBack = market.getReciprocal(Side.BACK);
        OptionalDouble reciprocalLay = market.getReciprocal(Side.LAY);
        Fairness fairness = calculator.getFairness(market);
        System.out.println("reciprocalBack = " + reciprocalBack);
        System.out.println("reciprocalLay = " + reciprocalLay);
        System.out.println("fairness = " + fairness);
        double bestBack = market.getBestPrices(Side.BACK).get(0).getAsDouble();
        double bestLay = market.getBestPrices(Side.LAY).get(0).getAsDouble();
        double reciprocalBackFairPrice = priceService.getReciprocalFairPrice(bestBack, reciprocalBack.getAsDouble());
        double reciprocalLayFairPrice = priceService.getReciprocalFairPrice(bestLay, reciprocalLay.getAsDouble());
        double fairnessBackFairPrice = priceService.getFairnessFairPrice(bestBack, fairness.get(Side.BACK).getAsDouble());
        double fairnessLayFairPrice = priceService.getFairnessFairPrice(bestLay, fairness.get(Side.LAY).getAsDouble());
        System.out.println("reciprocalBackFairPrice = " + reciprocalBackFairPrice);
        System.out.println("reciprocalLayFairPrice = " + reciprocalLayFairPrice);
        System.out.println("fairnessBackFairPrice = " + fairnessBackFairPrice);
        System.out.println("fairnessLayFairPrice = " + fairnessLayFairPrice);
        assertEquals(fairnessBackFairPrice, fairnessLayFairPrice, 0.01d);
        assertEquals(reciprocalBackFairPrice, reciprocalLayFairPrice, 0.1d);
    }

    private double getOverroundUnfairPrice(double fairPrice, double targetReciprocal, int winnerCount, int runnerCount) {
        double overround = winnerCount / targetReciprocal;
        double selectionOverround = (overround - winnerCount) / runnerCount;
        double probability = 1 / fairPrice;
        return Math.max(1 / (selectionOverround + probability), provider.getMinPrice());
    }


}
