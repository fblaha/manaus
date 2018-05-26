package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class MatchedAheadFunction implements AheadTimeFunction {

    @Override
    public Optional<Instant> getRelatedTime(SettledBet bet) {
        var matched = bet.getMatched();
        if (matched == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(matched.toInstant());
        }
    }

}
