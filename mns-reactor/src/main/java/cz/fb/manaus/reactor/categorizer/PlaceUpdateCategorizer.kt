package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class PlaceUpdateCategorizer : SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val action = settledBet.betAction
        if (action.betActionType == BetActionType.UPDATE) {
            return setOf("matchedAfter_update")
        } else if (action.betActionType == BetActionType.PLACE) {
            return setOf("matchedAfter_place")
        }
        return emptySet()
    }
}
