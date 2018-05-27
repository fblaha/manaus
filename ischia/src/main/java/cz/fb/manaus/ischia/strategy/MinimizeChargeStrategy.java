package cz.fb.manaus.ischia.strategy;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.google.common.primitives.Doubles;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import org.apache.commons.math3.util.Precision;

import java.util.OptionalDouble;

import static java.util.Objects.requireNonNull;

public class MinimizeChargeStrategy {

    final double fairnessReductionLow;
    final double fairnessReductionHighBack;
    final double fairnessReductionHighLay;

    public MinimizeChargeStrategy(double fairnessReductionLow, double fairnessReductionHighBack, double fairnessReductionHighLay) {
        this.fairnessReductionLow = fairnessReductionLow;
        this.fairnessReductionHighBack = fairnessReductionHighBack;
        this.fairnessReductionHighLay = fairnessReductionHighLay;
    }

    public double getReductionRate(BetContext context) {
        double rawRate = getRawRate(context);
        Preconditions.checkArgument(Range.closed(fairnessReductionLow, getUpperBoundary(context.getSide())).contains(rawRate));
        return rawRate;
    }

    double getUpperBoundary(Side side) {
        return requireNonNull(side) == Side.BACK ? fairnessReductionHighBack : fairnessReductionHighLay;
    }

    private double getRawRate(BetContext context) {
        OptionalDouble growthForecast = context.getChargeGrowthForecast();
        double upper = getUpperBoundary(context.getSide());
        if (growthForecast.isPresent()) {
            double growth = growthForecast.getAsDouble();
            if (Doubles.isFinite(growth)) {
                setActionProperty(context, growth);
                double result = Math.min(upper, upper * growth);
                return Math.max(fairnessReductionLow, result);
            }
        }
        return upper;
    }

    private void setActionProperty(BetContext context, double growth) {
        double rounded = Precision.round(growth, 4);
        context.getProperties().put("chargeGrowth", Double.toString(rounded));
    }

}
