package cz.fb.manaus.reactor.price;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.of;

@Component
public class LegacyFairnessCalculator {

    public static final double FAIR_EPS = 0.001d;
    private static final Logger log = Logger.getLogger(LegacyFairnessCalculator.class.getSimpleName());


    public double getFairness(double winnerCount, List<Double> bestPrices) {
        IllegalStateException exception = null;
        for (double maxValue : getMaxValues(winnerCount)) {
            try {
                return getFairness(winnerCount, 0d, maxValue, bestPrices);
            } catch (IllegalStateException e) {
                log.log(Level.WARNING, "Unable to compute fairness. Upper boundary ''{0}'' prices ''{1}''",
                        new Object[]{maxValue, bestPrices});
                exception = e;
            }
        }
        throw exception;
    }

    private ImmutableList<Double> getMaxValues(double winnerCount) {
        return of(winnerCount * 2, winnerCount * 4, winnerCount * 8, winnerCount * 16, winnerCount * 32);
    }

    private double getFairness(double winnerCount, double low, double high, List<Double> prices) {
        while (true) {
            double pivot = (low + high) / 2;
            double rightHandSide = getLayFairnessRightHandSide(pivot, prices);
            if (Precision.equals(rightHandSide, winnerCount, FAIR_EPS)) {
                return pivot;
            } else if (rightHandSide > winnerCount) {
                checkState(high > pivot, "fairness pivot high");
                high = pivot;
            } else {
                checkState(low < pivot, "fairness pivot low");
                low = pivot;
            }
        }
    }

    private double getLayFairnessRightHandSide(double fairness, List<Double> bestPrices) {
        return bestPrices.stream().mapToDouble(price -> 1 / (1 + (price - 1) / fairness)).sum();
    }


}
