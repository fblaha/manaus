package cz.fb.manaus.spring

import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
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
        return FlowFilter(0..0, { _, _ -> true }, emptySet())
    }

    @Bean
    open fun abnormalPriceFilter(priceBulldozer: PriceBulldozer): PriceFilter {
        return PriceFilter(3, 100.0, 0.0..100.0, priceBulldozer)
    }

}
