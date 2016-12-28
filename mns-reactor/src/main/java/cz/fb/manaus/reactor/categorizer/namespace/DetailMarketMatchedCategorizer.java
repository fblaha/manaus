package cz.fb.manaus.reactor.categorizer.namespace;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class DetailMarketMatchedCategorizer extends AbstractDetailMatchedCategorizer {

    public static final String NAMESPACE = "marketMatched";

    protected DetailMarketMatchedCategorizer() {
        super(NAMESPACE);
    }

    @Override
    protected double getMatchedAmount(SettledBet settledBet) {
        return settledBet.getBetAction()
                .getMarketPrices()
                .getRunnerPrices()
                .stream()
                .mapToDouble(RunnerPrices::getMatchedAmount)
                .sum();
    }
}
