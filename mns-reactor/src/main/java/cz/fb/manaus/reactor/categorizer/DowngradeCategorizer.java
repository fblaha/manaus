package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static cz.fb.manaus.core.model.PriceComparator.ORDERING;
import static java.util.Arrays.asList;

@Component
public class DowngradeCategorizer implements RelatedActionsAwareCategorizer {

    public static final String DOWNGRADE = "downgrade_true";
    public static final String DOWNGRADE_LAST = "downgradeLast_true";

    @Override
    public Set<String> getCategories(List<BetAction> actions, Market market) {
        Set<String> result = new HashSet<>();
        BetAction last = null;
        boolean lastDowngrade = false;
        for (BetAction action : actions) {
            lastDowngrade = false;
            if (last != null) {
                checkState(last.getPrice().getSide() == action.getPrice().getSide());
                checkState(last.getActionDate().before(action.getActionDate()));
                if (ORDERING.reverse().isStrictlyOrdered(asList(last.getPrice(), action.getPrice()))) {
                    lastDowngrade = true;
                    result.add(DOWNGRADE);
                }
            }
            last = action;
        }
        if (lastDowngrade) {
            result.add(DOWNGRADE_LAST);
        }
        return result;
    }
}