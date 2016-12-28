package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.OptionalDouble;

@Component
public class PlacedFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        Instant placed = bet.getPlaced().toInstant();
        Instant openDate = bet.getBetAction().getMarket().getEvent().getOpenDate().toInstant();
        double minutes = placed.until(openDate, ChronoUnit.MINUTES);
        return OptionalDouble.of(minutes / 60d);
    }

}
