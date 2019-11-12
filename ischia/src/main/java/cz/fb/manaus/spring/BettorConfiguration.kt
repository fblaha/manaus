package cz.fb.manaus.spring

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.PriceAdviser
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("ischia")
open class BettorConfiguration {

    @Bean
    @LayLoserBet
    open fun layAdviser(@LayLoserBet proposers: List<PriceProposer>): PriceAdviser {
        return ProposerAdviser(proposers)
    }

    @Bean
    @BackLoserBet
    open fun backAdviser(@BackLoserBet proposers: List<PriceProposer>): PriceAdviser {
        return ProposerAdviser(proposers)
    }

}
