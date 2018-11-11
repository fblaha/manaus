package cz.fb.manaus.reactor.categorizer

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.PriceComparator
import org.springframework.stereotype.Component

@Component
class DowngradeCategorizer : RelatedActionsAwareCategorizer {

    override fun getCategories(actions: List<BetAction>, market: Market): Set<String> {
        validate(actions)
        val result = mutableSetOf<String>()
        if (hasDowngrade(actions)) {
            result.add(DOWNGRADE)
        }
        val actionCount = actions.size
        if (actionCount >= 2 && hasDowngrade(actions.drop(actionCount - 2))) {
            result.add(DOWNGRADE_LAST)
        }
        return result
    }

    private fun hasDowngrade(actions: List<BetAction>): Boolean {
        return !Comparators.isInOrder(actions.map { it.price }, PriceComparator)
    }

    private fun validate(actions: List<BetAction>) {
        checkState(actions
                .map { it.price.side }
                .distinct().count() <= 1, "mixed sides")
        checkState(Comparators.isInStrictOrder(actions, compareBy { it.time }),
                "time disorder")
    }

    companion object {
        const val DOWNGRADE = "downgrade_true"
        const val DOWNGRADE_LAST = "downgradeLast_true"
    }
}
