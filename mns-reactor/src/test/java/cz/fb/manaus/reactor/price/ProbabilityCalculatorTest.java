package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProbabilityCalculatorTest extends AbstractLocalTestCase {

    @Autowired
    private ReactorTestFactory factory;
    @Autowired
    private ProbabilityCalculator calculator;
    @Autowired
    private FairnessPolynomialCalculator fairnessPolynomialCalculator;

    @Test
    public void testFromFairness() {
        checkProbability(List.of(0.6, 0.25, 0.15));
        checkProbability(List.of(0.4, 0.3, 0.3));
        checkProbability(List.of(0.6, 0.4));
        checkProbability(List.of(0.6, 0.2, 0.1, 0.1));
        checkProbability(List.of(0.9, 0.1));
    }

    private void checkProbability(List<Double> probabilities) {
        var rates = List.of(0.05, 0.1, 0.2, 0.4);
        for (double rate : rates) {
            var prices = factory.createMarket(rate, probabilities);
            var fairness = fairnessPolynomialCalculator.getFairness(prices);
            for (var side : Side.values()) {
                var probability = calculator.fromFairness(fairness.get(side).getAsDouble(), side, prices);
                for (var i = 0; i < probabilities.size(); i++) {
                    double expected = probabilities.get(i);
                    var selection = CoreTestFactory.HOME + i;
                    assertEquals(expected, probability.get(selection), 0.005);
                }
            }
        }
    }
}