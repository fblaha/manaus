package cz.fb.manaus.ischia.strategy

import com.google.common.base.Preconditions
import com.google.common.collect.Range
import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.BetContext
import org.apache.commons.math3.util.Precision

class MinimizeChargeStrategy(internal val fairnessReductionLow: Double, private val fairnessReductionHighBack: Double, private val fairnessReductionHighLay: Double) {

    fun getReductionRate(context: BetContext): Double {
        val rawRate = getRawRate(context)
        Preconditions.checkArgument(Range.closed(fairnessReductionLow, getUpperBoundary(context.side)).contains(rawRate))
        return rawRate
    }

    internal fun getUpperBoundary(side: Side): Double {
        return if (side === Side.BACK) fairnessReductionHighBack else fairnessReductionHighLay
    }

    private fun getRawRate(context: BetContext): Double {
        val growthForecast = context.chargeGrowthForecast
        val upper = getUpperBoundary(context.side)
        if (growthForecast != null) {
            if (Doubles.isFinite(growthForecast)) {
                setActionProperty(context, growthForecast)
                val result = Math.min(upper, upper * growthForecast)
                return Math.max(fairnessReductionLow, result)
            }
        }
        return upper
    }

    private fun setActionProperty(context: BetContext, growth: Double) {
        val rounded = Precision.round(growth, 4)
        context.properties["chargeGrowth"] = java.lang.Double.toString(rounded)
    }
}
