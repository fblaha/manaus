package cz.fb.manaus.reactor.price;

import com.google.common.base.MoreObjects;
import cz.fb.manaus.core.model.Side;

import java.util.Optional;
import java.util.OptionalDouble;

import static java.util.Objects.requireNonNull;

public class Fairness {
    private final OptionalDouble back;
    private final OptionalDouble lay;

    public Fairness(OptionalDouble back, OptionalDouble lay) {
        this.back = back;
        this.lay = lay;
    }

    public OptionalDouble getBack() {
        return back;
    }

    public OptionalDouble getLay() {
        return lay;
    }

    public OptionalDouble get(Side side) {
        return requireNonNull(side) == Side.BACK ? back : lay;
    }

    public Optional<Side> getMoreCredibleSide() {
        if (lay.isPresent() && back.isPresent()) {
            double layInverted = 1 / lay.getAsDouble();
            if (back.getAsDouble() > layInverted) {
                return Optional.of(Side.BACK);
            } else {
                return Optional.of(Side.LAY);
            }
        } else if (lay.isPresent()) {
            return Optional.of(Side.LAY);
        } else if (back.isPresent()) {
            return Optional.of(Side.BACK);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("back", back)
                .add("lay", lay)
                .toString();
    }
}
