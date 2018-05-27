package cz.fb.manaus.ischia.filter;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.betting.listener.FlowFilter;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class HappyFlowFilter extends FlowFilter {
    public HappyFlowFilter() {
        super(Range.all(), Range.all(), (market, runner) -> true, Set.of());
    }
}
