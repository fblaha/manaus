package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class ActionCountCategorizer implements RelatedActionsAwareCategorizer {

    private static RangeMap<Integer, String> RANGES = ImmutableRangeMap.<Integer, String>builder()
            .put(Range.singleton(1), "1")
            .put(Range.singleton(2), "2")
            .put(Range.singleton(3), "3")
            .put(Range.singleton(4), "4")
            .put(Range.closedOpen(5, 10), "5+")
            .put(Range.closedOpen(10, 20), "10+")
            .put(Range.closedOpen(20, 30), "20+")
            .put(Range.closedOpen(30, 40), "30+")
            .put(Range.closedOpen(40, 50), "40+")
            .put(Range.closedOpen(50, 100), "50+")
            .put(Range.downTo(100, BoundType.CLOSED), "100+")
            .build();


    @Override
    public Set<String> getCategories(List<BetAction> actions, Market market) {
        return Collections.singleton("betActionCount_" + RANGES.get(actions.size()));
    }

}
