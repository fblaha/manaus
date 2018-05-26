package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.OptionalDouble;

@Component
public class TimeDiffFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var placed = bet.getPlacedOrActionDate().toInstant();
        var actionDate = bet.getBetAction().getActionDate().toInstant();
        return OptionalDouble.of(actionDate.until(placed, ChronoUnit.SECONDS));
    }

}
