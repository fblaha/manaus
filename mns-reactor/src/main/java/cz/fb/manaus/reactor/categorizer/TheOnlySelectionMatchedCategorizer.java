package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;
import java.util.Set;

@Component
public class TheOnlySelectionMatchedCategorizer implements SettledBetCategorizer {

    @Autowired
    private SelectionActualMatchedCategorizer selectionActualMatchedCategorizer;
    @Autowired
    private ActualMatchedCategorizer actualMatchedCategorizer;

    @Override
    public boolean isMarketSnapshotRequired() {
        return true;
    }

    @Override
    public Set<String> getCategories(SettledBet bet, BetCoverage coverage) {
        OptionalDouble selectionMatched = selectionActualMatchedCategorizer.getAmount(bet);
        OptionalDouble allMatched = actualMatchedCategorizer.getAmount(bet);
        if (selectionMatched.isPresent() && allMatched.isPresent()) {
            boolean theOnlyMatched = Price.amountEq(allMatched.getAsDouble(), selectionMatched.getAsDouble());
            return Set.of("theOnlyMatched_" + theOnlyMatched);
        } else {
            return Set.of();
        }
    }
}
