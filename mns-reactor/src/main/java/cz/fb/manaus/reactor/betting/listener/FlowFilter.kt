package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Runner

open class FlowFilter(val indexRange: IntRange,
                      val runnerPredicate: (Market, Runner) -> Boolean,
                      val marketTypes: Set<String>) {

    companion object {
        val ALLOW_ALL = FlowFilter(0..9999, { _, _ -> true }, emptySet())
    }
}
