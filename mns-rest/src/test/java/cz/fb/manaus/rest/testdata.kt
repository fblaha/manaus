package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
object BestPriceProposer : AbstractBestPriceProposer(1)

@Component
object AcceptAllValidator : Validator {
    override fun validate(event: BetEvent): ValidationResult {
        return ValidationResult.OK
    }
}

@Configuration
open class TestLocalConfiguration {

    @Bean
    open fun priceAdviser(proposers: List<PriceProposer>): PriceAdviser {
        return ProposerAdviser(proposers)
    }

    @Bean
    open fun validationCoordinator(validators: List<Validator>, validationService: ValidationService): ValidationCoordinator {
        return ValidationCoordinator(validators, validationService)
    }

}


@Component
@Profile(ManausProfiles.DB)
class BackBettor(validationCoordinator: ValidationCoordinator, priceAdviser: PriceAdviser) : AbstractUpdatingBettor(
        side = Side.BACK,
        validationCoordinator = validationCoordinator,
        priceAdviser = priceAdviser)

