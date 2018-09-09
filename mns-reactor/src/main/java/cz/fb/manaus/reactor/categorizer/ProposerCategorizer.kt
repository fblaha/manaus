package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.ImmutableSet
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class ProposerCategorizer : AbstractProposerCategorizer() {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val proposers = getProposers(settledBet)
        val builder = ImmutableSet.builder<String>()
        val side = settledBet.price.side
        for (proposer in proposers) {
            builder.add(getSideAware("proposer_", side, proposer))
        }
        return builder.build()
    }

}
