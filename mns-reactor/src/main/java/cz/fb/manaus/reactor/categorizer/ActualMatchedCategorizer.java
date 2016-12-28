package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.FluentIterable;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

import static com.google.common.collect.FluentIterable.from;

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
        FluentIterable<RunnerPrices> runnerPrices = from(bet.getBetAction().getMarketPrices().getRunnerPrices())
                .filter(p -> p.getMatchedAmount() != null);
        if (runnerPrices.isEmpty()) {
            return OptionalDouble.empty();
        } else {
            return OptionalDouble.of(runnerPrices.toList().stream()
                    .mapToDouble(RunnerPrices::getMatchedAmount)
                    .sum());
        }
    }

}
