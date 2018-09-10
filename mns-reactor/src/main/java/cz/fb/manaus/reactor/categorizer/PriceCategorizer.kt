package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range.*
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.Objects.requireNonNull

@Component
class PriceCategorizer : SettledBetCategorizer {

    internal fun getCategory(price: Double): String {
        val suffix = requireNonNull<String>(CATEGORY_STEPS.get(price))
        return "priceRange_$suffix"
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        return setOf(getCategory(settledBet.price.price))
    }

    companion object {
        val CATEGORY_STEPS: RangeMap<Double, String> = ImmutableRangeMap.builder<Double, String>()
                .put(upTo(1.2, BoundType.OPEN), "1.0-1.2")
                .put(closedOpen(1.2, 1.5), "1.2-1.5")
                .put(closedOpen(1.5, 2.0), "1.5-2.0")
                .put(closedOpen(2.0, 2.5), "2.0-2.5")
                .put(closedOpen(2.5, 3.0), "2.5-3.0")
                .put(closedOpen(3.0, 4.0), "3.0-4.0")
                .put(closedOpen(4.0, 5.0), "4.0-5.0")
                .put(downTo(5.0, BoundType.CLOSED), "5.0+").build()
    }
}
