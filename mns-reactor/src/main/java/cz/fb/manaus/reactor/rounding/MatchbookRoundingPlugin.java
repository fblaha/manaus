package cz.fb.manaus.reactor.rounding;

import cz.fb.manaus.core.model.Price;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;


@Component
@Profile("matchbook")
public class MatchbookRoundingPlugin implements RoundingPlugin {

    @Override
    public OptionalDouble shift(double price, int stepNum) {
        return OptionalDouble.of(price + stepNum * getStep(price));
    }

    public double getStep(double price) {
        return (price - 1) * 0.02;
    }

    @Override
    public OptionalDouble round(double price) {
        return OptionalDouble.of(Price.round(price));
    }

}
