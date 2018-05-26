package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalDouble;

@Component
public class AgeDaysFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var openDate = bet.getBetAction().getMarket().getEvent().getOpenDate().toInstant();
        var days = Duration.between(openDate, Instant.now()).toDays();
        return OptionalDouble.of(days);
    }
}
