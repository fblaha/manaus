package cz.fb.manaus.spring

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetEventCoordinator
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposalService
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.reflect.full.findAnnotation

@Configuration
@Profile("ischia")
open class BettorConfiguration(
        private val metricRegistry: MetricRegistry,
        private val adviser: AmountAdviser,
        private val proposalService: PriceProposalService,
        private val roundingService: RoundingService

) {

    @Bean
    @LayLoserBet
    open fun layAdviser(@LayLoserBet proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<LayLoserBet>()) }
        return MinReduceProposerAdviser(proposers, adviser, proposalService, roundingService)
    }

    @Bean
    @LayLoserBet
    open fun layBetEventCoordinator(
            @LayLoserBet priceAdviser: PriceAdviser,
            @LayLoserBet validationCoordinator: ValidationCoordinator
    ): BetEventCoordinator {
        return BetEventCoordinator(Side.LAY, validationCoordinator, priceAdviser, metricRegistry)
    }

    @Bean
    @BackLoserBet
    open fun backAdviser(@BackLoserBet proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<BackLoserBet>()) }
        return MinReduceProposerAdviser(proposers, adviser, proposalService, roundingService)
    }

    @Bean
    @BackLoserBet
    open fun backBetEventCoordinator(@BackLoserBet priceAdviser: PriceAdviser,
                                     @BackLoserBet validationCoordinator: ValidationCoordinator
    ): BetEventCoordinator {
        return BetEventCoordinator(Side.BACK, validationCoordinator, priceAdviser, metricRegistry)
    }

}
