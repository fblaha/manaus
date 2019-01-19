package cz.fb.manaus.reactor.categorizer

import com.google.common.base.Joiner
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
open class ProposerCategorizer : RealizedBetCategorizer {

    private fun getSideAware(prefix: String, side: Side, category: String): String {
        return prefix + Joiner.on('.').join(side.name.toLowerCase(), category)
    }

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val proposers = realizedBet.betAction.proposers
        val side = realizedBet.settledBet.price.side
        return proposers.map { getSideAware("proposer_", side, it) }.toSet()
    }
}
