package cz.fb.manaus.reactor.betting.listener

import com.google.common.collect.Range
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.Runner

open class FlowFilter(val indexRange: Range<Int>,
                      val winnerCountRange: Range<Int>,
                      val runnerPredicate: (Market, Runner) -> Boolean,
                      val marketTypes: Set<String>) {
    companion object {

        val ALLOW_ALL = FlowFilter(Range.all(), Range.all(),
                { _, _ -> true }, emptySet())
    }
}
