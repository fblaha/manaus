package cz.fb.manaus.core.model;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.List;
import java.util.OptionalDouble;

import static java.util.stream.Collectors.toList;


public class TradedVolume {
    public static final TradedVolume EMPTY = new TradedVolume(List.of());

    private final List<Price> volume;

    public TradedVolume(Iterable<Price> volume) {
        this.volume = ImmutableList.copyOf(volume);
    }

    public static OptionalDouble getWeightedMean(List<Price> volume) {
        if (volume.isEmpty()) {
            return OptionalDouble.empty();
        } else {
            List<Double> prices = volume.stream().map(Price::getPrice).collect(toList());
            List<Double> amounts = volume.stream().map(Price::getAmount).collect(toList());
            return OptionalDouble.of(new Mean().evaluate(Doubles.toArray(prices), Doubles.toArray(amounts)));
        }
    }

    public OptionalDouble getWeightedMean() {
        return getWeightedMean(volume);
    }

    public List<Price> getVolume() {
        return volume;
    }
}
