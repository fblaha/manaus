package cz.fb.manaus.ischia.filter

import com.google.common.collect.Range
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ManausProfiles.PRODUCTION)
class MoneylineLoserFlowFilter : FlowFilter(Range.singleton(1), Range.singleton(1), { _, _ -> true }, setOf("moneyline"))
