package cz.fb.manaus.spring

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.listener.BetCoordinator
import cz.fb.manaus.reactor.betting.proposer.MinReduceProposerAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.validator.ValidationCoordinator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ischia")
open class BettorConfiguration {

    @Bean
    @LayLoserBet
    open fun layAdviser(@LayLoserBet proposers: List<PriceProposer>): PriceAdviser {
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    @LayLoserBet
    open fun layBettor(@LayLoserBet priceAdviser: PriceAdviser,
                       @LayLoserBet validationCoordinator: ValidationCoordinator): BetCoordinator {
        return BetCoordinator(Side.LAY, validationCoordinator, priceAdviser)
    }

    @Bean
    @BackLoserBet
    open fun backAdviser(@BackLoserBet proposers: List<PriceProposer>): PriceAdviser {
        return MinReduceProposerAdviser(proposers)
    }

    @Bean
    @BackLoserBet
    open fun backBettor(@BackLoserBet priceAdviser: PriceAdviser,
                        @BackLoserBet validationCoordinator: ValidationCoordinator): BetCoordinator {
        return BetCoordinator(Side.BACK, validationCoordinator, priceAdviser)
    }
}
