package cz.fb.manaus.reactor.rounding;

import java.util.OptionalDouble;

public interface RoundingPlugin {

    OptionalDouble shift(double price, int steps);

    OptionalDouble round(double price);
}
