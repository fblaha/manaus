package cz.fb.manaus.spring

import cz.fb.manaus.core.manager.MarketFilterService
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.BetManager
import cz.fb.manaus.reactor.betting.action.BetActionListener
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotListener
import cz.fb.manaus.reactor.price.PriceFilter
import cz.fb.manaus.spring.conf.BettingConf
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@ComponentScan("cz.fb.manaus.reactor")
@Profile(ManausProfiles.DB)
@EnableConfigurationProperties(BettingConf::class)
open class ReactorDatabaseConfiguration {

    @Bean
    open fun betManager(filterService: MarketFilterService,
                        priceFilter: PriceFilter?,
                        betActionRepository: BetActionRepository,
                        actionListeners: List<BetActionListener>,
                        bettingConf: BettingConf,
            // TODO not nullalble
                        snapshotListeners: List<MarketSnapshotListener>?): BetManager {
        val disabledListeners = bettingConf.disabledListeners.toSet()
        return BetManager.create(betActionRepository, filterService, priceFilter,
                disabledListeners, snapshotListeners, actionListeners)
    }
}