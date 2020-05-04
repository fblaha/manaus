package cz.fb.manaus.spring

import cz.fb.manaus.core.model.TYPE_MONEY_LINE
import cz.fb.manaus.core.provider.ProviderTag.VendorMatchbook
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.ischia.filter.moneyLineFilter
import cz.fb.manaus.ischia.filter.runnerNameFilter
import cz.fb.manaus.ischia.strategy.MinimizeChargeStrategy
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
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
    @BackUniverse
    open fun fixedBackDowngradeStrategy(): DowngradeStrategy {
        return FixedDowngradeStrategy(
                back = mapOf(TYPE_MONEY_LINE to 0.08).withDefault { 0.07 },
                lay = mapOf(TYPE_MONEY_LINE to 0.087).withDefault { 0.077 },
                tags = setOf(VendorMatchbook)
        )
    }

    @Bean
    @LayUniverse
    open fun fixedLayDowngradeStrategy(): DowngradeStrategy {
        return FixedDowngradeStrategy(
                back = mapOf(TYPE_MONEY_LINE to 0.077).withDefault { 0.067 },
                lay = mapOf(TYPE_MONEY_LINE to 0.087).withDefault { 0.077 },
                tags = setOf(VendorMatchbook)
        )
    }

    @Bean
    @LayUniverse
    @BackUniverse
    open fun minimizeChargeStrategy(): MinimizeChargeStrategy {
        return MinimizeChargeStrategy(
                fairnessReductionLow = 0.01,
                fairnessReductionHighBack = 0.05,
                fairnessReductionHighLay = 0.06
        )
    }

    @Bean
    open fun runnerNameFilter(marketRunnerConf: MarketRunnerConf): FlowFilter {
        val runnerName = marketRunnerConf.runnerName ?: ""
        return runnerNameFilter(runnerName)
    }

    @Bean
    open fun moneyLineFilter(): FlowFilter {
        return moneyLineFilter
    }

    @Bean
    open fun abnormalPriceFilter(priceConf: PriceConf, priceBulldozer: PriceBulldozer): PriceFilter {
        return PriceFilter(priceConf.limit,
                priceConf.bulldoze,
                priceConf.min..priceConf.max,
                priceBulldozer)
    }
}
