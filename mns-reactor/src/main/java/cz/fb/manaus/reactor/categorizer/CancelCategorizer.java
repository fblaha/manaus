package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.indexOfSubList;

@Component
public class CancelCategorizer implements RelatedActionsAwareCategorizer {

    public static final String PREFIX = "cancel_";

    @Override
    public Set<String> getCategories(List<BetAction> actions, Market market) {
        if (actions.isEmpty()) return Set.of();
        var types = actions.stream().map(BetAction::getBetActionType)
                .collect(Collectors.toCollection(LinkedList::new));
        if (types.getFirst() == BetActionType.PLACE) {
            types.removeFirst();
        }
        var cancel = indexOfSubList(types, List.of(BetActionType.UPDATE, BetActionType.PLACE)) != -1;
        return Set.of(PREFIX + Boolean.toString(cancel));
    }

}
