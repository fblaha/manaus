package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class MatchedAmountDiffFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        double actionAmount = bet.getBetAction().getPrice().getAmount();
        double betAmount = bet.getPrice().getAmount();
        return OptionalDouble.of(actionAmount - betAmount);
    }

}
