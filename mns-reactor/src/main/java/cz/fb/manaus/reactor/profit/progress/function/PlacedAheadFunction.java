package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PlacedAheadFunction implements AheadTimeFunction {

    @Override
    public Optional<Instant> getRelatedTime(SettledBet bet) {
        return Optional.ofNullable(bet.getPlacedOrActionDate().toInstant());
    }

}
