package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.Range.closedOpen
import com.google.common.collect.Range.upTo
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.repository.domain.RealizedBet
import cz.fb.manaus.core.repository.domain.Side
import org.apache.commons.math3.util.Precision
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class ReciprocalCategorizer : RealizedBetCategorizer {

    @Autowired(required = false)
    private val customReciprocalRangeSupplier: CustomReciprocalRangeSupplier? = null

    override val isMarketSnapshotRequired: Boolean = true

    private fun handleCustomRange(reciprocal: Double, result: MutableSet<String>) {
        if (customReciprocalRangeSupplier != null) {
            val customRange = customReciprocalRangeSupplier.customRanges.get(reciprocal)
            if (customRange != null) {
                result.add(RECIPROCAL + customRange)
            }
        }
    }

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val reciprocal = realizedBet.betAction.marketPrices.getReciprocal(Side.BACK)
        if (reciprocal.isPresent) {
            val result = mutableSetOf<String>()
            handleCustomRange(reciprocal.asDouble, result)
            val rounded = Precision.round(reciprocal.asDouble, 2)
            val strRange = Objects.requireNonNull<String>(RANGES.get(rounded), reciprocal.toString())
            result.add(RECIPROCAL + strRange)
            return result
        }
        return emptySet()
    }

    companion object {
        val RANGES: RangeMap<Double, String> = ImmutableRangeMap.builder<Double, String>()
                .put(Range.downTo(1.0, BoundType.CLOSED), "1.00+")
                .put(Range.singleton(0.99), "0.99")
                .put(Range.singleton(0.98), "0.98")
                .put(Range.singleton(0.97), "0.97")
                .put(Range.singleton(0.96), "0.96")
                .put(Range.singleton(0.95), "0.95")
                .put(Range.singleton(0.94), "0.94")
                .put(Range.singleton(0.93), "0.93")
                .put(Range.singleton(0.92), "0.92")
                .put(Range.singleton(0.91), "0.91")
                .put(Range.singleton(0.90), "0.90")
                .put(closedOpen(0.85, 0.90), "0.85-0.90")
                .put(closedOpen(0.80, 0.85), "0.80-0.85")
                .put(closedOpen(0.75, 0.80), "0.75-0.80")
                .put(closedOpen(0.70, 0.75), "0.70-0.75")
                .put(closedOpen(0.60, 0.70), "0.60-0.70")
                .put(closedOpen(0.50, 0.60), "0.50-0.60")
                .put(closedOpen(0.30, 0.50), "0.30-0.50")
                .put(upTo(0.30, BoundType.OPEN), "0.00-0.30")
                .build()

        private const val RECIPROCAL = "reciprocal_"
    }
}
