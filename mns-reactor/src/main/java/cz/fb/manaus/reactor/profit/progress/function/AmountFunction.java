package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class AmountFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        return OptionalDouble.of(bet.getPrice().getAmount());
    }

}
