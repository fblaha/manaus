package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class SelectionActualMatchedCategorizer extends AbstractMatchedCategorizer {

    public SelectionActualMatchedCategorizer() {
        super("actualMatchedSelection_");
    }

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    protected OptionalDouble getAmount(SettledBet bet) {
        Double matchedAmount = bet.getBetAction()
                .getMarketPrices()
                .getRunnerPrices(bet.getSelectionId())
                .getMatchedAmount();
        return matchedAmount == null ? OptionalDouble.empty() : OptionalDouble.of(matchedAmount);
    }

}
