package cz.fb.manaus.spring

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
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
    private val adviser: AmountAdviser,
    private val proposalService: PriceProposalService,
    private val roundingService: RoundingService

) {

    @Bean
    @LayUniverse
    open fun layAdviser(@LayUniverse proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<LayUniverse>()) }
        return MinReduceProposerAdviser(proposers, adviser, proposalService, roundingService)
    }

    @Bean
    @LayUniverse
    open fun layBetEventCoordinator(
        @LayUniverse priceAdviser: PriceAdviser,
        @LayUniverse validationCoordinator: ValidationCoordinator
    ): BetEventCoordinator =
        BetEventCoordinator(Side.LAY, validationCoordinator, priceAdviser)

    @Bean
    @BackUniverse
    open fun backAdviser(@BackUniverse proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<BackUniverse>()) }
        return MinReduceProposerAdviser(proposers, adviser, proposalService, roundingService)
    }

    @Bean
    @BackUniverse
    open fun backBetEventCoordinator(
        @BackUniverse priceAdviser: PriceAdviser,
        @BackUniverse validationCoordinator: ValidationCoordinator
    ): BetEventCoordinator =
        BetEventCoordinator(Side.BACK, validationCoordinator, priceAdviser)

}
