package cz.fb.manaus.ischia.strategy

import com.google.common.base.Preconditions
import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import org.apache.commons.math3.util.Precision

class MinimizeChargeStrategy(internal val fairnessReductionLow: Double, private val fairnessReductionHighBack: Double, private val fairnessReductionHighLay: Double) {

    fun getReductionRate(context: BetContext): Double {
        val rawRate = getRawRate(context)
        Preconditions.checkArgument(rawRate in fairnessReductionLow..getUpperBoundary(context.side))
        return rawRate
    }

    internal fun getUpperBoundary(side: Side): Double {
        return if (side === Side.BACK) fairnessReductionHighBack else fairnessReductionHighLay
    }

    private fun getRawRate(context: BetContext): Double {
        val growthForecast = context.chargeGrowthForecast
        val upper = getUpperBoundary(context.side)
        return if (growthForecast != null && Doubles.isFinite(growthForecast)) {
            setActionProperty(context, growthForecast)
            val result = Math.min(upper, upper * growthForecast)
            Math.max(fairnessReductionLow, result)
        } else upper
    }

    private fun setActionProperty(context: BetContext, growth: Double) {
        val rounded = Precision.round(growth, 4)
        context.properties["chargeGrowth"] = rounded.toString()
    }
}
