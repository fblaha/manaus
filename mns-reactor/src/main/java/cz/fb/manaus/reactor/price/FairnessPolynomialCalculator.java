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
            List<Double> presentPrices = prices.stream().map(OptionalDouble::getAsDouble).collect(toList());
            PolynomialFunction rightSide = multiplyPolynomials(presentPrices);
            rightSide = rightSide.multiply(new PolynomialFunction(new double[]{winnerCount}));

            List<PolynomialFunction> leftSideItems = new LinkedList<>();
            for (int i = 0; i < presentPrices.size(); i++) {
                List<Double> otherPrices = new LinkedList<>();
                for (int j = 0; j < presentPrices.size(); j++) {
                    if (i != j) {
                        otherPrices.add(presentPrices.get(j));
                    }
                }
                leftSideItems.add(multiplyPolynomials(otherPrices));
            }
            PolynomialFunction leftSide = leftSideItems.stream().reduce((p1, p2) -> p1.add(p2)).get();
            PolynomialFunction equation = leftSide.subtract(rightSide);

            LaguerreSolver laguerreSolver = new LaguerreSolver();
            try {
                double root = laguerreSolver.solve(100, equation, 0, 1000);
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
        return polynomials.stream().reduce((p1, p2) -> p1.multiply(p2)).get();
    }
}
