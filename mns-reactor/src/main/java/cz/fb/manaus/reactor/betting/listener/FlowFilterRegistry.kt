package cz.fb.manaus.reactor.betting.listener

import org.springframework.stereotype.Component

@Component
class FlowFilterRegistry(flowFilters: List<FlowFilter>) {

    private val byMarketType: Map<String, FlowFilter>

    init {
        val m = flowFilters.flatMap { it.marketTypes.map { type -> type to it } }.toMap()
        val defaultFilter = flowFilters.find { it.marketTypes.isEmpty() } ?: FlowFilter.ALLOW_ALL
        byMarketType = m.withDefault { defaultFilter }
    }

    fun getFlowFilter(marketType: String): FlowFilter {
        return byMarketType.getValue(marketType)
    }
}
