package cz.fb.manaus.spring

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetEventCoordinator
import cz.fb.manaus.reactor.betting.listener.BetEventListener
import cz.fb.manaus.reactor.betting.listener.FlowFilter
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotCoordinator
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import cz.fb.manaus.reactor.betting.validator.ValidationService
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.price.PriceBulldozer
import cz.fb.manaus.reactor.price.PriceFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("manila")
@ComponentScan(value = ["cz.fb.manaus.manila"])
open class ManilaLocalConfiguration {

    @Bean
    open fun bestChanceFlowFilter(): FlowFilter {
        return FlowFilter(0..0, { _, _ -> true }, emptySet())
    }

    @Bean
    open fun abnormalPriceFilter(priceBulldozer: PriceBulldozer): PriceFilter {
        return PriceFilter(3, 100.0, 0.0..100.0, priceBulldozer)
    }

    @Bean
    @ManilaBet
    open fun adviser(proposers: List<PriceProposer>): PriceAdviser {
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    @ManilaBet
    open fun validationCoordinator(validationService: ValidationService, validators: List<Validator>): ValidationCoordinator {
        return ValidationCoordinator(validators, validationService)
    }

    @Bean
    @ManilaBet
    open fun betEventCoordinator(priceAdviser: PriceAdviser, validationCoordinator: ValidationCoordinator): BetEventCoordinator {
        return BetEventCoordinator(validationCoordinator, priceAdviser)
    }

    @Bean
    @ManilaBet
    open fun layBettor(betEventListener: BetEventListener): MarketSnapshotCoordinator {
        return MarketSnapshotCoordinator(
                side = Side.LAY,
                betEventListener = betEventListener
        )
    }

}
