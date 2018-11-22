package cz.fb.manaus.ischia.filter

import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.PRODUCTION)
class DrawFlowFilter : FlowFilter(ALL_INDICES,
        { _, runner -> "draw" in runner.name.toLowerCase() }, emptySet())
