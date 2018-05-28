package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static cz.fb.manaus.core.model.PriceComparator.ORDERING;
import static java.util.Comparator.comparing;

@Component
public class DowngradeCategorizer implements RelatedActionsAwareCategorizer {

    public static final String DOWNGRADE = "downgrade_true";
    public static final String DOWNGRADE_LAST = "downgradeLast_true";

    @Override
    public Set<String> getCategories(List<BetAction> actions, Market market) {
        validate(actions);
        var result = new HashSet<String>();
        if (hasDowngrade(actions.stream())) {
            result.add(DOWNGRADE);
        }
        var actionCount = actions.size();
        if (actionCount >= 2 && hasDowngrade(actions.stream().skip(actionCount - 2))) {
            result.add(DOWNGRADE_LAST);
        }
        return result;
    }

    private boolean hasDowngrade(Stream<BetAction> actions) {
        return !ORDERING.isOrdered(actions.map(BetAction::getPrice).collect(Collectors.toList()));
    }

    private void validate(List<BetAction> actions) {
        checkState(actions.stream()
                .map(BetAction::getPrice)
                .map(Price::getSide)
                .distinct().count() <= 1, "mixed sides");
        checkState(Ordering.from(comparing(BetAction::getActionDate)).isStrictlyOrdered(actions),
                "time disorder");
    }
}
