package cz.fb.manaus.ischia.strategy

import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ProviderCapability
import cz.fb.manaus.core.provider.ProviderSelector
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.proposer.DowngradeStrategy
import kotlin.math.max
import kotlin.math.min

class MinimizeChargeStrategy(
        internal val fairnessReductionLow: Double,
        private val fairnessReductionHighBack: Double,
        private val fairnessReductionHighLay: Double,
        override val tags: Set<String> = emptySet()) : DowngradeStrategy, ProviderSelector {

    override fun invoke(ctx: BetContext): Double {
        val rawRate = getRawRate(ctx)
        require(rawRate in fairnessReductionLow..getUpperBoundary(ctx.side))
        return rawRate
    }

    internal fun getUpperBoundary(side: Side): Double {
        return if (side === Side.BACK) fairnessReductionHighBack else fairnessReductionHighLay
    }

    private fun getRawRate(context: BetContext): Double {
        val growthForecast = context.metrics.chargeGrowthForecast
        val upper = getUpperBoundary(context.side)
        return if (growthForecast != null && Doubles.isFinite(growthForecast)) {
            val result = min(upper, upper * growthForecast)
            max(fairnessReductionLow, result)
        } else upper
    }

    override val capabilities get() = setOf(ProviderCapability.CommissionNetWin)
}
