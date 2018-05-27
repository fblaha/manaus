package cz.fb.manaus.ischia.filter;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.betting.listener.FlowFilter;
import cz.fb.manaus.spring.CoreLocalConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile(CoreLocalConfiguration.PRODUCTION_PROFILE)
public class MoneylineLoserFlowFilter extends FlowFilter {

    public MoneylineLoserFlowFilter() {
        super(Range.singleton(1), Range.singleton(1),
                (market, runner) -> true, Set.of("moneyline"));
    }

}
