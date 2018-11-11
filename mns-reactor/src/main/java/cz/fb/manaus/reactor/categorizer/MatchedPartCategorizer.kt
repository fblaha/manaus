package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class MatchedPartCategorizer : RealizedBetCategorizer {

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val matched = realizedBet.settledBet.price.amount
        val requested = realizedBet.betAction.price.amount
        return if (Price.amountEq(matched, requested)) {
            setOf(PREFIX + "full")
        } else {
            setOf(PREFIX + "partial")
        }
    }

    companion object {
        const val PREFIX = "matchedPart_"
    }

}
