package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;

import java.util.List;
import java.util.Set;

public interface RelatedActionsAwareCategorizer {

    Set<String> getCategories(List<BetAction> actions, Market market);

}
