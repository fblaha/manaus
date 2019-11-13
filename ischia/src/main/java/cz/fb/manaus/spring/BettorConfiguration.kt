package cz.fb.manaus.spring

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetEventCoordinator
import cz.fb.manaus.reactor.betting.listener.BetEventExplorer
import cz.fb.manaus.reactor.betting.listener.BetEventListener
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.reflect.full.findAnnotation

@Configuration
@Profile("ischia")
open class BettorConfiguration {

    @Bean
    @LayLoserBet
    open fun layAdviser(@LayLoserBet proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<LayLoserBet>()) }
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    @LayLoserBet
    open fun layBetEventCoordinator(@LayLoserBet priceAdviser: PriceAdviser,
                                    @LayLoserBet validationCoordinator: ValidationCoordinator): BetEventCoordinator {
        return BetEventCoordinator(validationCoordinator, priceAdviser)
    }

    @Bean
    @LayLoserBet
    open fun layBettor(@LayLoserBet betEventListener: BetEventListener): BetEventExplorer {
        return BetEventExplorer(Side.LAY, betEventListener)
    }

    @Bean
    @BackLoserBet
    open fun backAdviser(@BackLoserBet proposers: List<PriceProposer>): PriceAdviser {
        check(proposers.isNotEmpty())
        proposers.forEach { checkNotNull(it::class.findAnnotation<BackLoserBet>()) }
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    @BackLoserBet
    open fun backBetEventCoordinator(@BackLoserBet priceAdviser: PriceAdviser,
                                     @BackLoserBet validationCoordinator: ValidationCoordinator): BetEventCoordinator {
        return BetEventCoordinator(validationCoordinator, priceAdviser)
    }

    @Bean
    @BackLoserBet
    open fun backBettor(@BackLoserBet betEventListener: BetEventListener): BetEventExplorer {
        return BetEventExplorer(Side.BACK, betEventListener)
    }
}
