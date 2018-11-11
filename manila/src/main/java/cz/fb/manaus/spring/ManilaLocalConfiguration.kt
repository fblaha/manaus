package cz.fb.manaus.spring

import com.google.common.collect.Range
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("manila")
@ComponentScan(value = ["cz.fb.manaus.manila"])
open class ManilaLocalConfiguration {

    @Bean
    open fun bestChanceFlowFilter(): FlowFilter {
        return FlowFilter(Range.singleton(0), { _, _ -> true }, emptySet())
    }

}
