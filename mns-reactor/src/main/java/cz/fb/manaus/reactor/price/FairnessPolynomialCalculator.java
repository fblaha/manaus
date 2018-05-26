package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Side;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.exception.NoBracketingException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;

import static java.util.stream.Collectors.toList;

@Component
public class FairnessPolynomialCalculator {

    private PolynomialFunction toPolynomial(double price) {
        return new PolynomialFunction(new double[]{1, price - 1});
    }

    public OptionalDouble getFairness(double winnerCount, List<OptionalDouble> prices) {
        if (prices.stream().allMatch(OptionalDouble::isPresent)) {
            var presentPrices = prices.stream().map(OptionalDouble::getAsDouble).collect(toList());
            var rightSide = multiplyPolynomials(presentPrices);
            rightSide = rightSide.multiply(new PolynomialFunction(new double[]{winnerCount}));

            var leftSideItems = new LinkedList<PolynomialFunction>();
            for (var i = 0; i < presentPrices.size(); i++) {
                var otherPrices = new LinkedList<Double>();
                for (var j = 0; j < presentPrices.size(); j++) {
                    if (i != j) {
                        otherPrices.add(presentPrices.get(j));
                    }
                }
                leftSideItems.add(multiplyPolynomials(otherPrices));
            }
            var leftSide = leftSideItems.stream().reduce(PolynomialFunction::add).get();
            var equation = leftSide.subtract(rightSide);

            var laguerreSolver = new LaguerreSolver();
            try {
                var root = laguerreSolver.solve(100, equation, 0, 1000);
                return OptionalDouble.of(1 / root);
            } catch (NoBracketingException exception) {
                return OptionalDouble.empty();
            }
        } else {
            return OptionalDouble.empty();
        }
    }

    public Fairness getFairness(MarketPrices marketPrices) {
        return new Fairness(
                getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(Side.BACK)),
                getFairness(marketPrices.getWinnerCount(), marketPrices.getBestPrices(Side.LAY)));
    }

    private PolynomialFunction multiplyPolynomials(List<Double> prices) {
        List<PolynomialFunction> polynomials = prices.stream().map(this::toPolynomial).collect(toList());
        return polynomials.stream().reduce(PolynomialFunction::multiply).get();
    }
}
