package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AmountAdviser
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetEventCoordinator
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposalService
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.BestPriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class BestPriceProposer(roundingService: RoundingService)
    : PriceProposer by BestPriceProposer(1, roundingService)

@Component
object AcceptAllValidator : Validator {
    override fun validate(event: BetEvent): ValidationResult {
        return ValidationResult.OK
    }
}

@Configuration
open class TestLocalConfiguration(
        private val adviser: AmountAdviser,
        private val proposalService: PriceProposalService,
        private val roundingService: RoundingService

) {

    @Bean
    open fun priceAdviser(proposers: List<PriceProposer>): PriceAdviser {
        return MinReduceProposerAdviser(proposers, adviser, proposalService, roundingService)
    }

    @Bean
    open fun validationCoordinator(validators: List<Validator>, validationService: ValidationService): ValidationCoordinator {
        return ValidationCoordinator(validators, validationService)
    }

    @Bean
    open fun backBetEventCoordinator(priceAdviser: PriceAdviser,
                                     validationCoordinator: ValidationCoordinator): BetEventCoordinator {
        return BetEventCoordinator(Side.BACK, validationCoordinator, priceAdviser)
    }

}

