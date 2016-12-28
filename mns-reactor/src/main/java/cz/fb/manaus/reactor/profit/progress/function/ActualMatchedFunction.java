package cz.fb.manaus.reactor.profit.progress.function;

import com.google.common.collect.FluentIterable;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

import static com.google.common.collect.FluentIterable.from;

@Component
public class ActualMatchedFunction implements ProgressFunction {

    @Override
    public OptionalDouble function(SettledBet bet) {
        FluentIterable<RunnerPrices> runnerPrices = from(bet.getBetAction().getMarketPrices().getRunnerPrices())
                .filter(p -> p.getMatchedAmount() != null);
        return OptionalDouble.of(runnerPrices.toList().stream()
                .mapToDouble(RunnerPrices::getMatchedAmount)
                .sum());
    }

}
