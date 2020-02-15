package cz.fb.manaus.spring

import cz.fb.manaus.core.manager.MarketSnapshotEventValidationService
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.MarketSnapshotNotifier
import cz.fb.manaus.reactor.betting.action.BetCommandHandler
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
    open fun marketSnapshotNotifier(marketSnapshotEventValidationService: MarketSnapshotEventValidationService,
                                    priceFilter: PriceFilter?,
                                    handlers: List<BetCommandHandler>,
                                    bettingConf: BettingConf,
                                    betActionRepository: BetActionRepository,
                                    snapshotListeners: List<MarketSnapshotListener>): MarketSnapshotNotifier {
        return MarketSnapshotNotifier(
                snapshotListeners,
                marketSnapshotEventValidationService,
                handlers
        )
    }
}