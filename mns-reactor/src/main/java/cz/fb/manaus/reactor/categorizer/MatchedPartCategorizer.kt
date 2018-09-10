package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class MatchedPartCategorizer : SettledBetCategorizer {

    override fun isSimulationSupported(): Boolean {
        return false
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val matched = settledBet.price.amount
        val requested = settledBet.betAction.price.amount
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
