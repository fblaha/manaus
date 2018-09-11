package cz.fb.manaus.reactor.categorizer

import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Ordering
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.PriceComparator.ORDERING
import org.springframework.stereotype.Component
import java.util.*
import java.util.Comparator.comparing

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
        return !ORDERING.isOrdered(actions.map { it.price })
    }

    private fun validate(actions: List<BetAction>) {
        checkState(actions
                .map { it.price }
                .map { it.side }
                .distinct().count() <= 1, "mixed sides")
        checkState(Ordering.from(comparing<BetAction, Date>({ it.actionDate })).isStrictlyOrdered(actions),
                "time disorder")
    }

    companion object {
        const val DOWNGRADE = "downgrade_true"
        const val DOWNGRADE_LAST = "downgradeLast_true"
    }
}
