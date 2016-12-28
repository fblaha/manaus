package cz.fb.manaus.reactor.categorizer.namespace;

import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

@Component
public class DetailSelectionMatchedCategorizer extends AbstractDetailMatchedCategorizer {

    public static final String NAMESPACE = "selectionMatched";

    protected DetailSelectionMatchedCategorizer() {
        super(NAMESPACE);
    }

    @Override
    protected double getMatchedAmount(SettledBet settledBet) {
        RunnerPrices runnerPrices = settledBet.getBetAction().getMarketPrices()
                .getRunnerPrices(settledBet.getSelectionId());
        return runnerPrices.getMatchedAmount();
    }
}
