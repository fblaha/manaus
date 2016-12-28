package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class PlaceUpdateCategorizer implements SettledBetCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        BetAction action = settledBet.getBetAction();
        if (action.getBetActionType() == BetActionType.UPDATE) {
            return Collections.singleton("matchedAfter_update");
        } else if (action.getBetActionType() == BetActionType.PLACE) {
            return Collections.singleton("matchedAfter_place");
        }
        return Collections.emptySet();
    }
}
