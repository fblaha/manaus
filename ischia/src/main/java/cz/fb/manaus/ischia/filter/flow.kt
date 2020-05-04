package cz.fb.manaus.ischia.filter

import cz.fb.manaus.core.model.TYPE_MONEY_LINE
import cz.fb.manaus.reactor.betting.listener.FlowFilter


fun runnerNameFilter(runnerName: String): FlowFilter {
    return FlowFilter(IntRange.EMPTY,
            { _, runner -> runnerName.toLowerCase() in runner.name.toLowerCase() },
            emptySet())
}

val moneyLineFilter = FlowFilter(IntRange.EMPTY, { _, _ -> true }, setOf(TYPE_MONEY_LINE))

