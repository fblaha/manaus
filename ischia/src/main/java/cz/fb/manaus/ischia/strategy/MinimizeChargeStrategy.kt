package cz.fb.manaus.ischia.strategy

import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderSelector
import cz.fb.manaus.core.provider.ProviderTag.VendorBetfair
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import kotlin.math.max
import kotlin.math.min

class MinimizeChargeStrategy(
        internal val fairnessReductionLow: Double,
        private val fairnessReductionHighBack: Double,
        private val fairnessReductionHighLay: Double
) : DowngradeStrategy, ProviderSelector {

    override fun invoke(event: BetEvent): Double {
        val rawRate = getRawRate(event)
        require(rawRate in fairnessReductionLow..getUpperBoundary(event.side))
        return rawRate
    }

    internal fun getUpperBoundary(side: Side): Double {
        return if (side == Side.BACK) fairnessReductionHighBack else fairnessReductionHighLay
    }

    private fun getRawRate(event: BetEvent): Double {
        val growthForecast = event.metrics.chargeGrowthForecast
        val upper = getUpperBoundary(event.side)
        return if (growthForecast != null && Doubles.isFinite(growthForecast)) {
            val result = min(upper, upper * growthForecast)
            max(fairnessReductionLow, result)
        } else upper
    }

    override val tags get() = setOf(VendorBetfair)
}
