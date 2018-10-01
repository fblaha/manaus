package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class SideCategorizer : SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val side = settledBet.price.side.name.toLowerCase()
        return setOf("side_$side")
    }

}
