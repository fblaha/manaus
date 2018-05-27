package cz.fb.manaus.ischia.filter;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Runner;
import cz.fb.manaus.reactor.betting.listener.FlowFilter;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile(CoreLocalConfiguration.PRODUCTION_PROFILE)
public class DrawFlowFilter extends FlowFilter {

    public DrawFlowFilter() {
        super(Range.all(), Range.singleton(1), DrawFlowFilter::isDraw, Set.of());
    }

    private static boolean isDraw(Market market, Runner runner) {
        return runner.getName().toLowerCase().contains("draw");
    }
}
