package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.TYPE_MATCH_ODDS
import org.springframework.stereotype.Component

@Component
class MatchOddsCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val market = realizedBet.market
        if (market.type != TYPE_MATCH_ODDS) return emptySet()
        val runner = market.runners.first { it.selectionId == realizedBet.settledBet.selectionId }
        val runnerName = when (runner.sortPriority) {
            0 -> "home"
            1 -> "away"
            2 -> "draw"
            else -> "undefined"
        }
        return setOf("matchOdds_$runnerName")
    }
}
