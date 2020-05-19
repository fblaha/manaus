package cz.fb.manaus.spring

import cz.fb.manaus.core.model.TYPE_HANDICAP
import cz.fb.manaus.core.model.TYPE_MONEY_LINE
import cz.fb.manaus.core.model.TYPE_TOTAL
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
    open fun runnerNameFilter(marketRunnerConf: MarketRunnerConf): FlowFilter =
            runnerNameFilter(marketRunnerConf.runnerName ?: "")

    @Bean
    open fun moneyLineFilter(): FlowFilter =
            FlowFilter(IntRange.EMPTY, { _, _ -> true }, setOf(TYPE_MONEY_LINE, TYPE_TOTAL, TYPE_HANDICAP))

    @Bean
    open fun abnormalPriceFilter(priceConf: PriceConf, priceBulldozer: PriceBulldozer): PriceFilter =
            PriceFilter(
                    priceConf.limit,
                    priceConf.bulldoze,
                    priceConf.min..priceConf.max,
                    priceBulldozer
            )
}

private fun runnerNameFilter(runnerName: String): FlowFilter =
        FlowFilter(
                IntRange.EMPTY,
                { _, runner -> runnerName.toLowerCase() in runner.name.toLowerCase() },
                emptySet()
        )
