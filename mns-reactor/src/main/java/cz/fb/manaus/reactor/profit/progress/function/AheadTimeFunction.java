package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.OptionalDouble;

public interface AheadTimeFunction extends ProgressFunction {

    @Override
    default OptionalDouble apply(SettledBet bet) {
        var eventTime = getRelatedTime(bet);
        if (eventTime.isPresent()) {
            var openDate = bet.getBetAction().getMarket().getEvent().getOpenDate().toInstant();
            var minutes = eventTime.get().until(openDate, ChronoUnit.MINUTES);
            return OptionalDouble.of(minutes / 60d);
        } else {
            return OptionalDouble.empty();
        }
    }

    Optional<Instant> getRelatedTime(SettledBet bet);

}
