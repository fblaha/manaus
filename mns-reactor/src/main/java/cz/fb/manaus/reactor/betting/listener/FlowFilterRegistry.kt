package cz.fb.manaus.reactor.betting.listener

import org.springframework.stereotype.Component

@Component
class FlowFilterRegistry(flowFilters: List<FlowFilter>) {

    private val byMarketType: Map<String, FlowFilter>
    private val defaultFilter: FlowFilter

    init {
        val m = mutableMapOf<String, FlowFilter>()
        for (flowFilter in flowFilters) {
            for (type in flowFilter.marketTypes) {
                m[type] = flowFilter
            }
        }
        defaultFilter = flowFilters.find { it.marketTypes.isEmpty() } ?: FlowFilter.ALLOW_ALL
        byMarketType = m.toMap()
    }

    fun getFlowFilter(marketType: String): FlowFilter {
        return byMarketType.getOrDefault(marketType, defaultFilter)
    }
}
