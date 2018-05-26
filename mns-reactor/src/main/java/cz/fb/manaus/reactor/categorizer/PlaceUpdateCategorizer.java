package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PlaceUpdateCategorizer implements SettledBetCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var action = settledBet.getBetAction();
        if (action.getBetActionType() == BetActionType.UPDATE) {
            return Set.of("matchedAfter_update");
        } else if (action.getBetActionType() == BetActionType.PLACE) {
            return Set.of("matchedAfter_place");
        }
        return Set.of();
    }
}
