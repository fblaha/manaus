package cz.fb.manaus.reactor.rounding;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.model.Price;
import org.apache.commons.math3.util.Precision;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Range.closedOpen;
import static com.google.common.collect.Range.openClosed;

@Component
@Profile("betfair")
public class BetfairRoundingPlugin implements RoundingPlugin {

    public static final RangeMap<Double, Double> INCREMENT_STEPS = ImmutableRangeMap.<Double, Double>builder()
            .put(closedOpen(1d, 2d), 0.01d)
            .put(closedOpen(2d, 3d), 0.02d)
            .put(closedOpen(3d, 4d), 0.05d)
            .put(closedOpen(4d, 6d), 0.1)
            .put(closedOpen(6d, 10d), 0.2d)
            .put(closedOpen(10d, 20d), 0.5d)
            .put(closedOpen(20d, 30d), 1d)
            .put(closedOpen(30d, 50d), 2d)
            .put(closedOpen(50d, 100d), 5d)
            .put(closedOpen(100d, 1000d), 10d).build();
    public static final RangeMap<Double, Double> DECREMENT_STEPS = ImmutableRangeMap.<Double, Double>builder()
            .put(openClosed(1d, 2d), -0.01d)
            .put(openClosed(2d, 3d), -0.02d)
            .put(openClosed(3d, 4d), -0.05d)
            .put(openClosed(4d, 6d), -0.1)
            .put(openClosed(6d, 10d), -0.2d)
            .put(openClosed(10d, 20d), -0.5d)
            .put(openClosed(20d, 30d), -1d)
            .put(openClosed(30d, 50d), -2d)
            .put(openClosed(50d, 100d), -5d)
            .put(openClosed(100d, 1000d), -10d).build();


    private OptionalDouble getStep(double price, boolean increment) {
        Double step;
        if (increment) {
            step = INCREMENT_STEPS.get(price);
        } else {
            step = DECREMENT_STEPS.get(price);
        }
        return step == null ? OptionalDouble.empty() : OptionalDouble.of(step);
    }

    @Override
    public OptionalDouble shift(double price, int steps) {
        Preconditions.checkArgument(steps != 0);
        var increment = steps > 0;
        return shift(price, Math.abs(steps), increment);
    }

    private OptionalDouble shift(double price, int steps, boolean increment) {
        checkArgument(steps >= 1);
        var step = getStep(price, increment);
        if (step.isPresent()) {
            var result = Price.round(price + step.getAsDouble());
            if (steps == 1) {
                return OptionalDouble.of(result);
            } else {
                return shift(result, steps - 1, increment);
            }
        } else {
            return OptionalDouble.empty();
        }
    }

    @Override
    public OptionalDouble round(double price) {
        var step = getStep(price, true);
        if (step.isPresent()) {
            var rest = Precision.round(price % step.getAsDouble(), 6);
            var complement = step.getAsDouble() - rest;
            if (rest >= complement) {
                price += complement;
            } else {
                price -= rest;
            }
            return OptionalDouble.of(Price.round(price));
        } else {
            return OptionalDouble.empty();
        }
    }

}
