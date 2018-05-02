package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class ActualSelectionMatchedFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        Double matchedAmount = bet.getBetAction()
                .getMarketPrices()
                .getRunnerPrices(bet.getSelectionId())
                .getMatchedAmount();
        return matchedAmount == null ? OptionalDouble.empty() : OptionalDouble.of(matchedAmount);
    }

}
