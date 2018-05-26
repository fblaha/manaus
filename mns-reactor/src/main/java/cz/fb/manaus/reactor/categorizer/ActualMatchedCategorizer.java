package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class ActualMatchedCategorizer extends AbstractMatchedCategorizer {

    public ActualMatchedCategorizer() {
        super("actualMatchedMarket_");
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    protected OptionalDouble getAmount(SettledBet bet) {
        var runnerPrices = bet.getBetAction()
                .getMarketPrices().getRunnerPrices().stream()
                .filter(p -> p.getMatchedAmount() != null).collect(Collectors.toList());
        if (runnerPrices.isEmpty()) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(runnerPrices.stream()
                    .mapToDouble(RunnerPrices::getMatchedAmount)
                    .sum());
        }
    }

}
