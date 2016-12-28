package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class TotalMatchedFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        Double matchedAmount = bet.getBetAction().getMarket().getMatchedAmount();
        return matchedAmount == null ? OptionalDouble.empty() : OptionalDouble.of(matchedAmount);
    }

}
