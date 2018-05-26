package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class ActualMatchedFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var prices = bet.getBetAction().getMarketPrices().getRunnerPrices();
        double sum = prices.stream()
                .filter(p -> p.getMatchedAmount() != null)
                .mapToDouble(RunnerPrices::getMatchedAmount)
                .sum();
        return OptionalDouble.of(sum);
    }

}
