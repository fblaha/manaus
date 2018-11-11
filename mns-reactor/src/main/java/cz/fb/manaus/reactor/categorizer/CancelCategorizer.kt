package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.BetActionType
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.Collections.indexOfSubList

@Component
class CancelCategorizer : RelatedActionsAwareCategorizer {

    override fun getCategories(actions: List<BetAction>, market: Market): Set<String> {
        if (actions.isEmpty()) return emptySet()
        var types = actions.map { it.betActionType }

        if (types.first() == BetActionType.PLACE) {
            types = types.drop(1)
        }
        val cancel = indexOfSubList(types, listOf(BetActionType.UPDATE, BetActionType.PLACE)) != -1
        return setOf(PREFIX + java.lang.Boolean.toString(cancel))
    }

    companion object {
        const val PREFIX = "cancel_"
    }

}
