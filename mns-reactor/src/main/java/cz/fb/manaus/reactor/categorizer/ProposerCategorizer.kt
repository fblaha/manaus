package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.ImmutableSet
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class ProposerCategorizer : AbstractProposerCategorizer() {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val proposers = getProposers(realizedBet)
        val builder = ImmutableSet.builder<String>()
        val side = realizedBet.settledBet.price.side
        for (proposer in proposers) {
            builder.add(getSideAware("proposer_", side, proposer))
        }
        return builder.build()
    }

}
