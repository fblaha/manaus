package cz.fb.manaus.reactor.betting.listener

import org.springframework.stereotype.Component

@Component
class FlowFilterRegistry(flowFilters: List<FlowFilter>) {

    private val byMarketType: Map<String, FlowFilter>

    init {
        val m = mutableMapOf<String, FlowFilter>()
        for (flowFilter in flowFilters) {
            for (type in flowFilter.marketTypes) {
                m[type] = flowFilter
            }
        }
        val defaultFilter = flowFilters.find { it.marketTypes.isEmpty() } ?: FlowFilter.ALLOW_ALL
        byMarketType = m.toMap().withDefault { defaultFilter }
    }

    fun getFlowFilter(marketType: String): FlowFilter {
        return byMarketType.getValue(marketType)
    }
}
