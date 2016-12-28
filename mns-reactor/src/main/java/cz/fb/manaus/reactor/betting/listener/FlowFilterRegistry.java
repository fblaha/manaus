package cz.fb.manaus.reactor.betting.listener;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Lazy
@Component
public class FlowFilterRegistry {

    private final Map<String, FlowFilter> byMarketType;
    private FlowFilter defaultFilter;

    @Autowired
    public FlowFilterRegistry(List<FlowFilter> flowFilters) {
        ImmutableMap.Builder<String, FlowFilter> builder = ImmutableMap.builder();
        flowFilters.forEach(flowFilter -> flowFilter.getMarketTypes().forEach(
                type -> builder.put(type, flowFilter)));
        defaultFilter = flowFilters.stream()
                .filter(flowFilter -> flowFilter.getMarketTypes().isEmpty())
                .findAny().orElse(FlowFilter.ALLOW_ALL);
        byMarketType = builder.build();
    }

    public FlowFilter getFlowFilter(String marketType) {
        return byMarketType.getOrDefault(marketType, defaultFilter);
    }

}
