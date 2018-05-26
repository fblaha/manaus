package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;

import java.util.OptionalDouble;

public abstract class AbstractOfferedAmountFunction implements ProgressFunction {

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var runnerPrices = getRunnerPrices(bet);
        var sum = runnerPrices.getPrices().stream().mapToDouble(Price::getAmount).sum();
        return OptionalDouble.of(sum);
    }

    protected abstract RunnerPrices getRunnerPrices(SettledBet bet);
}
