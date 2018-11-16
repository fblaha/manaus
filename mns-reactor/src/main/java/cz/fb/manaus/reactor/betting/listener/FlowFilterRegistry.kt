package cz.fb.manaus.reactor.betting.listener

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

// TODO no lazy
@Lazy
@Component
class FlowFilterRegistry(flowFilters: List<FlowFilter>) {

    private val byMarketType: Map<String, FlowFilter>
    private val defaultFilter: FlowFilter

    init {
        val m = mutableMapOf<String, FlowFilter>()
        flowFilters.forEach { flowFilter -> flowFilter.marketTypes.forEach { m[it] = flowFilter } }
        defaultFilter = flowFilters
                .find { flowFilter -> flowFilter.marketTypes.isEmpty() }
                ?: FlowFilter.ALLOW_ALL
        byMarketType = m.toMap()
    }

    fun getFlowFilter(marketType: String): FlowFilter {
        return byMarketType.getOrDefault(marketType, defaultFilter)
    }
}
