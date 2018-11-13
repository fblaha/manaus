package cz.fb.manaus.reactor.betting.listener

import com.google.common.collect.ImmutableMap
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

// TODO no lazy
@Lazy
@Component
class FlowFilterRegistry(flowFilters: List<FlowFilter>) {

    private val byMarketType: Map<String, FlowFilter>
    private val defaultFilter: FlowFilter

    init {
        val builder = ImmutableMap.builder<String, FlowFilter>()
        flowFilters.forEach { flowFilter -> flowFilter.marketTypes.forEach { type -> builder.put(type, flowFilter) } }
        defaultFilter = flowFilters
                .find { flowFilter -> flowFilter.marketTypes.isEmpty() }
                ?: FlowFilter.ALLOW_ALL
        byMarketType = builder.build()
    }

    fun getFlowFilter(marketType: String): FlowFilter {
        return byMarketType.getOrDefault(marketType, defaultFilter)
    }

}
