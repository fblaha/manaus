package cz.fb.manaus.ischia.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import org.springframework.stereotype.Component


@Component
class HappyFlowFilter : FlowFilter(Range.all(), { _, _ -> true }, setOf())
