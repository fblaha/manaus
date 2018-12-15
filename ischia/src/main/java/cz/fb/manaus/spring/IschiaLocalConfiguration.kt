package cz.fb.manaus.spring

import cz.fb.manaus.ischia.filter.MarketTypeFilter
import cz.fb.manaus.ischia.filter.moneyLineLoserFilter
import cz.fb.manaus.ischia.filter.runnerNameFilter
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
@Import(BetfairStrategyConfiguration::class, MatchbookStrategyConfiguration::class)
@EnableConfigurationProperties(MarketRunnerConf::class, PriceConf::class)
open class IschiaLocalConfiguration {

    @Bean
    open fun marketTypeFilter(marketRunnerConf: MarketRunnerConf): MarketTypeFilter {
        return MarketTypeFilter(marketRunnerConf.types!!.toSet())
    }

    @Bean
    open fun runnerNameFilter(marketRunnerConf: MarketRunnerConf): FlowFilter {
        val runnerName = marketRunnerConf.runnerName ?: ""
        return runnerNameFilter(runnerName)
    }

    @Bean
    open fun moneyLineLoserFilter(): FlowFilter {
        return moneyLineLoserFilter
    }

    @Bean
    open fun abnormalPriceFilter(priceConf: PriceConf, priceBulldozer: PriceBulldozer): PriceFilter {
        return PriceFilter(priceConf.limit,
                priceConf.bulldoze,
                priceConf.min..priceConf.max,
                priceBulldozer)
    }
}
