package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.BoundType
import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.Range.closedOpen
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Objects.requireNonNull

@Component
class FairnessCategorizer : SettledBetCategorizer {

    @Autowired
    private lateinit var calculator: FairnessPolynomialCalculator

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val marketPrices = settledBet.betAction.marketPrices
        val fairness = calculator.getFairness(marketPrices.winnerCount.toDouble(),
                marketPrices.getBestPrices(Side.BACK)).asDouble
        return setOf(getCategory(fairness))
    }

    internal fun getCategory(fairness: Double): String {
        return PREFIX + requireNonNull<String>(RANGES.get(fairness))
    }

    companion object {
        val RANGES: RangeMap<Double, String> = ImmutableRangeMap.builder<Double, String>()
                .put(Range.downTo(1.0, BoundType.CLOSED), "1.00+")
                .put(closedOpen(0.95, 1.00), "0.95-1.00")
                .put(closedOpen(0.90, 0.95), "0.90-0.95")
                .put(closedOpen(0.85, 0.90), "0.85-0.90")
                .put(closedOpen(0.80, 0.85), "0.80-0.85")
                .put(closedOpen(0.75, 0.80), "0.75-0.80")
                .put(closedOpen(0.70, 0.75), "0.70-0.75")

                .put(closedOpen(0.6, 0.7), "0.60-0.70")
                .put(closedOpen(0.5, 0.6), "0.50-0.60")
                .put(closedOpen(0.4, 0.5), "0.40-0.50")
                .put(closedOpen(0.3, 0.4), "0.30-0.40")
                .put(closedOpen(0.2, 0.3), "0.20-0.30")
                .put(closedOpen(0.1, 0.2), "0.10-0.20")
                .put(closedOpen(0.0, 0.1), "0.00-0.10").build()
        const val PREFIX = "fairness_"
    }
}
