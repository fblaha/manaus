package cz.fb.manaus.reactor.rounding;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalDouble;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Service
public class RoundingService {

    @Autowired
    private RoundingPlugin plugin;
    @Autowired
    private ExchangeProvider provider;

    public OptionalDouble increment(double price, int stepNum) {
        OptionalDouble result = plugin.shift(price, stepNum);
        if (result.isPresent()) checkState(result.getAsDouble() > price);
        return result;
    }

    public OptionalDouble decrement(double price, int stepNum) {
        OptionalDouble result = plugin.shift(price, -stepNum);
        if (result.isPresent()) {
            checkState(result.getAsDouble() < price);
            if (result.getAsDouble() < provider.getMinPrice()) {
                return OptionalDouble.empty();
            }
        }
        return result;
    }

    public OptionalDouble downgrade(double price, int stepNum, Side side) {
        if (checkNotNull(side) == Side.LAY) {
            return decrement(price, stepNum);
        } else if (side == Side.BACK) {
            return increment(price, stepNum);
        }
        throw new IllegalStateException();
    }

    public Optional<Price> increment(Price price, int stepNum) {
        OptionalDouble newPrice = increment(price.getPrice(), stepNum);
        if (newPrice.isPresent()) {
            return Optional.of(new Price(newPrice.getAsDouble(), price.getAmount(), price.getSide()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Price> decrement(Price price, int stepNum) {
        OptionalDouble newPrice = decrement(price.getPrice(), stepNum);
        if (newPrice.isPresent()) {
            return Optional.of(new Price(newPrice.getAsDouble(), price.getAmount(), price.getSide()));
        } else {
            return Optional.empty();
        }
    }

    public OptionalDouble roundBet(double price) {
        return plugin.round(price);
    }

}
