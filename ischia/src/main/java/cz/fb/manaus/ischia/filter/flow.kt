package cz.fb.manaus.ischia.filter

import cz.fb.manaus.reactor.betting.listener.FlowFilter


fun runnerNameFilter(runnerName: String): FlowFilter {
    return FlowFilter(FlowFilter.ALL_INDICES,
            { _, runner -> runnerName.toLowerCase() in runner.name.toLowerCase() },
            emptySet())
}

val moneyLineLoserFilter = FlowFilter(1..1, { _, _ -> true }, setOf("moneyline"))

