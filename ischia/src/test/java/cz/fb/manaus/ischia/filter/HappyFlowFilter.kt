package cz.fb.manaus.ischia.filter

import cz.fb.manaus.reactor.betting.listener.FlowFilter
import org.springframework.stereotype.Component


@Component
class HappyFlowFilter : FlowFilter(0..9999, { _, _ -> true }, setOf())
