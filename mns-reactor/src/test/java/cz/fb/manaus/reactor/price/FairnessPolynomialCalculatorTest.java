package cz.fb.manaus.reactor.price;

import com.google.common.base.Function;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class FairnessPolynomialCalculatorTest extends AbstractLocalTestCase {
    public static final List<Double> BEST_PRICES_HARD = List.of(
            85.0, 510.0, 270.0, 700.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            1000.0, 1000.0, 1000.0, 1000.0, 38.0, 95.0, 110.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0,
            1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 1000.0);


    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private LegacyFairnessCalculator legacyCalculator;

    private List<OptionalDouble> toOptional(List<Double> values) {
        return values.stream().map(OptionalDouble::of).collect(Collectors.toList());
    }

    @Test
    public void testPolynomialFairness() {
        assertEquals(0.866d, calculator.getFairness(1, toOptional(List.of(2.5, 1.5))).getAsDouble(), 0.001d);
        assertEquals(0.825d, calculator.getFairness(1, toOptional(List.of(2.7, 1.4))).getAsDouble(), 0.001d);
        assertEquals(0.75d, calculator.getFairness(1, toOptional(List.of(2.5, 2.5, 2.5))).getAsDouble(), 0.001d);
        assertEquals(0.85d, calculator.getFairness(1, toOptional(List.of(2.7, 2.7, 2.7))).getAsDouble(), 0.001d);
    }

    @Test
    public void testFairnessHard() {
        var fairness = calculator.getFairness(1, toOptional(BEST_PRICES_HARD)).getAsDouble();
        assertTrue(fairness > 0);
    }

    @Test
    public void testFairnessOneWinner() {
        assertThat(calculator.getFairness(1, toOptional(List.of(3d, 3d, 3d))).getAsDouble(), is(1d));
    }

    @Test
    public void testFairnessTwoWinners() {
        assertEquals(1d, calculator.getFairness(2,
                toOptional(List.of(1.5d, 1.5d, 1.5d))).getAsDouble(), 0.0001d);
    }

    @Test
    public void testFairnessTwoWinnersCompare() {
        assertTrue(calculator.getFairness(2, toOptional(List.of(1.4d, 1.5d, 1.5d))).getAsDouble() <
                calculator.getFairness(2, toOptional(List.of(1.5d, 1.5d, 1.5d))).getAsDouble());
    }

    @Test
    public void testFairnessLayTwoRunners() {
        assertEquals(1.118d, calculator.getFairness(1, toOptional(List.of(1.5, 3.5))).getAsDouble(), 0.001d);
    }

    @Test
    public void testFairnessLayThreeRunners() {
        assertEquals(1.093, calculator.getFairness(1, toOptional(List.of(3.5, 3.5, 2.7))).getAsDouble(), 0.001d);
    }

    @Test
    public void testLegacyComparison() {
        checkLegacyComparison(price -> List.of(price, price, 2.5));
        checkLegacyComparison(price -> List.of(2.5, price, 2.5));
        checkLegacyComparison(price -> List.of(price, price, price));
        checkLegacyComparison(price -> List.of(3d, 3d, price));
        checkLegacyComparison(price -> List.of(1.5, 2.5, price));
    }

    private void checkLegacyComparison(Function<Double, List<Double>> priceFunc) {
        for (var increment = 0.1; increment < 1.5; increment += 0.1) {
            var price = 1.5 + increment;
            var prices = priceFunc.apply(price);
            var fairness = calculator.getFairness(1, toOptional(prices)).getAsDouble();
            var legacy = legacyCalculator.getFairness(1, prices);
            assertEquals(legacy, fairness, 0.005d);
        }
    }

}