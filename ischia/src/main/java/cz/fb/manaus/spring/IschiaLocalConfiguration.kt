package cz.fb.manaus.spring

import cz.fb.manaus.ischia.filter.MarketTypeFilter
import cz.fb.manaus.ischia.filter.moneyLineLoserFilter
import cz.fb.manaus.ischia.filter.runnerNameFilter
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.spring.conf.FilterConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*

@Configuration
@Profile("ischia")
@ComponentScan(value = ["cz.fb.manaus.ischia"])
@Import(BetfairValues::class, MatchbookValues::class)
@EnableConfigurationProperties(FilterConf::class)
open class IschiaLocalConfiguration {

    @Bean
    open fun marketTypeFilter(filterConf: FilterConf): MarketTypeFilter {
        return MarketTypeFilter(filterConf.marketTypes!!.toSet())
    }

    @Bean
    open fun runnerNameFilter(filterConf: FilterConf): FlowFilter {
        val runnerName = filterConf.runnerName ?: ""
        return runnerNameFilter(runnerName)
    }

    @Bean
    open fun moneyLineLoserFilter(): FlowFilter {
        return moneyLineLoserFilter
    }
}
