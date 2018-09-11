package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class ActionCountCategorizer : RelatedActionsAwareCategorizer {

    override fun getCategories(actions: List<BetAction>, market: Market): Set<String> {
        return setOf("betActionCount_" + RANGES.get(actions.size)!!)
    }

    companion object {
        private val RANGES = ImmutableRangeMap.builder<Int, String>()
                .put(Range.singleton(1), "1")
                .put(Range.singleton(2), "2")
                .put(Range.singleton(3), "3")
                .put(Range.singleton(4), "4")
                .put(Range.closedOpen(5, 10), "5+")
                .put(Range.closedOpen(10, 20), "10+")
                .put(Range.closedOpen(20, 30), "20+")
                .put(Range.closedOpen(30, 40), "30+")
                .put(Range.closedOpen(40, 50), "40+")
                .put(Range.closedOpen(50, 100), "50+")
                .put(Range.downTo(100, BoundType.CLOSED), "100+")
                .build()
    }
}
