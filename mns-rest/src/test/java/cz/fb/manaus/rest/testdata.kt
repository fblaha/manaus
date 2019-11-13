package cz.fb.manaus.rest

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetEventCoordinator
import cz.fb.manaus.reactor.betting.listener.BetEventListener
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotCoordinator
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.common.AbstractBestPriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    open fun validationCoordinator(validators: List<Validator>, validationService: ValidationService): ValidationCoordinator {
        return ValidationCoordinator(validators, validationService)
    }

    @Bean
    open fun backBetEventCoordinator(priceAdviser: PriceAdviser,
                                     validationCoordinator: ValidationCoordinator): BetEventCoordinator {
        return BetEventCoordinator(validationCoordinator, priceAdviser)
    }

    @Bean
    open fun backBettor(betEventListener: BetEventListener): MarketSnapshotCoordinator {
        return MarketSnapshotCoordinator(
                side = Side.BACK,
                betEventListener = betEventListener
        )
    }

}

