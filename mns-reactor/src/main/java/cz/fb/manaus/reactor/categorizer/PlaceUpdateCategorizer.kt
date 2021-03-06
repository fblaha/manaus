package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class PlaceUpdateCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val action = realizedBet.betAction
        if (action.betActionType == BetActionType.UPDATE) {
            return setOf("matchedAfter_update")
        } else if (action.betActionType == BetActionType.PLACE) {
            return setOf("matchedAfter_place")
        }
        return emptySet()
    }
}
