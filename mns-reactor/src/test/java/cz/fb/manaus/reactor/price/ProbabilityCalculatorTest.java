package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ProbabilityCalculatorTest extends AbstractLocalTestCase {

    @Autowired
    private ReactorTestFactory factory;
    @Autowired
    private ProbabilityCalculator calculator;
    @Autowired
    private FairnessPolynomialCalculator fairnessPolynomialCalculator;

    @Test
    public void testFromFairness() throws Exception {
        checkProbability(Arrays.asList(0.6, 0.25, 0.15));
        checkProbability(Arrays.asList(0.4, 0.3, 0.3));
        checkProbability(Arrays.asList(0.6, 0.4));
        checkProbability(Arrays.asList(0.6, 0.2, 0.1, 0.1));
        checkProbability(Arrays.asList(0.9, 0.1));
    }

    private void checkProbability(List<Double> probabilities) {
        List<Double> rates = Arrays.asList(0.05, 0.1, 0.2, 0.4);
        for (double rate : rates) {
            MarketPrices prices = factory.createMarket(rate, probabilities);
            Fairness fairness = fairnessPolynomialCalculator.getFairness(prices);
            for (Side side : Side.values()) {
                Map<Long, Double> probability = calculator.fromFairness(fairness.get(side).getAsDouble(), side, prices);
                for (int i = 0; i < probabilities.size(); i++) {
                    double expected = probabilities.get(i);
                    long selection = CoreTestFactory.HOME + i;
                    assertEquals(expected, probability.get(selection), 0.005);
                }
            }
        }
    }

}