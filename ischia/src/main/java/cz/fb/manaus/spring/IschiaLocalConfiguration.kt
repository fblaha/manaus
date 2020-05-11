package cz.fb.manaus.spring

import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
import cz.fb.manaus.spring.conf.MarketRunnerConf
import cz.fb.manaus.spring.conf.PriceConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*


@Configuration
@Profile("ischia")
@ComponentScan(value = ["cz.fb.manaus.ischia"])
@Import(ValidationConfiguration::class, BettorConfiguration::class)
@EnableConfigurationProperties(MarketRunnerConf::class, PriceConf::class)
open class IschiaLocalConfiguration {

    @Bean
    open fun nopFilter(): FlowFilter {
        return FlowFilter(IntRange.EMPTY, { _, _ -> true }, emptySet())
    }

    @Bean
    open fun abnormalPriceFilter(priceConf: PriceConf, priceBulldozer: PriceBulldozer): PriceFilter {
        return PriceFilter(priceConf.limit,
                priceConf.bulldoze,
                priceConf.min..priceConf.max,
                priceBulldozer)
    }
}
