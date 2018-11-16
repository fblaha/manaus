package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class ProposerCategorizer : AbstractProposerCategorizer() {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val proposers = getProposers(realizedBet)
        val builder = mutableSetOf<String>()
        val side = realizedBet.settledBet.price.side
        for (proposer in proposers) {
            builder.add(getSideAware("proposer_", side, proposer))
        }
        return builder.toSet()
    }

}
