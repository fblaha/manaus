package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import java.util.*

abstract class AbstractMatchedCategorizer protected constructor(private val prefix: String) : SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val amount = getAmount(settledBet)
        return if (amount.isPresent) setOf(getCategory(amount.asDouble)) else emptySet()
    }

    abstract fun getAmount(settledBet: SettledBet): OptionalDouble

    internal fun getCategory(matchedAmount: Double): String {
        return prefix + CATEGORY_STEPS.get(matchedAmount)!!
    }

    companion object {
        val CATEGORY_STEPS: RangeMap<Double, String> = ImmutableRangeMap.builder<Double, String>()
                .put(Range.closed(0.0, 10.0), "0-10")
                .put(Range.openClosed(10.0, 100.0), "10-100")
                .put(Range.openClosed(100.0, 1000.0), "100-1k")
                .put(Range.openClosed(1000.0, 10_000.0), "1k-10k")
                .put(Range.openClosed(10_000.0, 100_000.0), "10k-100k")
                .put(Range.openClosed(100_000.0, 1000_000.0), "100k-1M")
                .put(Range.openClosed(1000_000.0, 10_000_000.0), "1M-10M")
                .put(Range.downTo(10_000_000.0, BoundType.OPEN), "10M+")
                .build()
    }
}
