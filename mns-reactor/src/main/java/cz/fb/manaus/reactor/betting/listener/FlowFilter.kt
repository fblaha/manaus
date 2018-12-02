package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Runner

data class FlowFilter(val indexRange: IntRange,
                      val runnerPredicate: (Market, Runner) -> Boolean,
                      val marketTypes: Set<String>) {

    companion object {
        val ALL_INDICES = 0..9999
        val ALLOW_ALL = FlowFilter(ALL_INDICES, { _, _ -> true }, emptySet())
    }
}
