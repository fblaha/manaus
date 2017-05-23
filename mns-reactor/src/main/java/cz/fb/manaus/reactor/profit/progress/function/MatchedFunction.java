package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class MatchedFunction implements RelativeTimeFunction {

    @Override
    public Optional<Instant> getRelatedTime(SettledBet bet) {
        Date matched = bet.getMatched();
        if (matched == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(matched.toInstant());
        }
    }

}
