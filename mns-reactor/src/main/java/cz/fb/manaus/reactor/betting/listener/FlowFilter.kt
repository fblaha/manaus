package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Runner

data class FlowFilter(val indexRange: IntRange,
                      val runnerPredicate: (Market, Runner) -> Boolean,
                      val marketTypes: Set<String>) {

    val checkIndex: Boolean = !indexRange.isEmpty()

    fun acceptIndex(index: Int): Boolean {
        return indexRange.isEmpty() || index in indexRange
    }

    companion object {
        val ALLOW_ALL = FlowFilter(IntRange.EMPTY, { _, _ -> true }, emptySet())
    }
}
