package cz.fb.manaus.reactor.categorizer;


import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MatchedPartCategorizer implements SettledBetCategorizer {

    public static final String PREFIX = "matchedPart_";

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var matched = settledBet.getPrice().getAmount();
        var requested = settledBet.getBetAction().getPrice().getAmount();
        if (Price.amountEq(matched, requested)) {
            return Set.of(PREFIX + "full");
        } else {
            return Set.of(PREFIX + "partial");
        }
    }

}
