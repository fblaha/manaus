package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.OptionalDouble;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class AgeDaysFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var openDate = bet.getBetAction().getMarket().getEvent().getOpenDate().toInstant();
        var days = DAYS.between(openDate, Instant.now());
        return OptionalDouble.of(days);
    }
}
