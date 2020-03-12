package cz.fb.manaus.spring

import cz.fb.manaus.core.provider.ProviderTag.VendorMatchbook
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.ischia.filter.moneyLineLoserFilter
import cz.fb.manaus.ischia.filter.runnerNameFilter
import cz.fb.manaus.ischia.strategy.MinimizeChargeStrategy
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
import cz.fb.manaus.spring.conf.MarketRunnerConf
import cz.fb.manaus.spring.conf.PriceConf
import cz.fb.manaus.spring.conf.ProposerConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.*

@Configuration
@Profile("ischia")
@ComponentScan(value = ["cz.fb.manaus.ischia"])
@Import(ValidationConfiguration::class, BettorConfiguration::class)
@EnableConfigurationProperties(MarketRunnerConf::class, PriceConf::class, ProposerConf::class)
open class IschiaLocalConfiguration {

    @Bean
    @BackUniverse
    open fun fixedBackDowngradeStrategy(proposerConf: ProposerConf): DowngradeStrategy {
        return FixedDowngradeStrategy(
                back = proposerConf.downgradeBackProposerBackPrice,
                lay = proposerConf.downgradeBackProposerLayPrice,
                tags = setOf(VendorMatchbook)
        )
    }

    @Bean
    @LayUniverse
    open fun fixedLayDowngradeStrategy(proposerConf: ProposerConf): DowngradeStrategy {
        return FixedDowngradeStrategy(
                back = proposerConf.downgradeLayProposerBackPrice,
                lay = proposerConf.downgradeLayProposerLayPrice,
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
