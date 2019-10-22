package cz.fb.manaus.reactor.rounding

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range.closedOpen
import com.google.common.collect.Range.openClosed
import com.google.common.collect.RangeMap
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.provider.ProviderCapability.PriceShiftFixedStep
import org.apache.commons.math3.util.Precision
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class RangeStepRoundingPlugin : RoundingPlugin {

    private val incrementSteps: RangeMap<Double, Double> = ImmutableRangeMap.builder<Double, Double>()
            .put(closedOpen(1.0, 2.0), 0.01)
            .put(closedOpen(2.0, 3.0), 0.02)
            .put(closedOpen(3.0, 4.0), 0.05)
            .put(closedOpen(4.0, 6.0), 0.1)
            .put(closedOpen(6.0, 10.0), 0.2)
            .put(closedOpen(10.0, 20.0), 0.5)
            .put(closedOpen(20.0, 30.0), 1.0)
            .put(closedOpen(30.0, 50.0), 2.0)
            .put(closedOpen(50.0, 100.0), 5.0)
            .put(closedOpen(100.0, 1000.0), 10.0).build()

    private val decrementSteps: RangeMap<Double, Double> = ImmutableRangeMap.builder<Double, Double>()
            .put(openClosed(1.0, 2.0), -0.01)
            .put(openClosed(2.0, 3.0), -0.02)
            .put(openClosed(3.0, 4.0), -0.05)
            .put(openClosed(4.0, 6.0), -0.1)
            .put(openClosed(6.0, 10.0), -0.2)
            .put(openClosed(10.0, 20.0), -0.5)
            .put(openClosed(20.0, 30.0), -1.0)
            .put(openClosed(30.0, 50.0), -2.0)
            .put(openClosed(50.0, 100.0), -5.0)
            .put(openClosed(100.0, 1000.0), -10.0).build()


    private fun getStep(price: Double, increment: Boolean): Double? {
        return if (increment) {
            incrementSteps.get(price)
        } else {
            decrementSteps.get(price)
        }
    }

    override fun shift(price: Double, steps: Int): Double? {
        require(steps != 0)
        val increment = steps > 0
        return shift(price, abs(steps), increment)
    }

    private fun shift(price: Double, steps: Int, increment: Boolean): Double? {
        require(steps >= 1)
        val step = getStep(price, increment)
        return if (step != null) {
            val result = Price.round(price + step)
            if (steps == 1) {
                result
            } else {
                shift(result, steps - 1, increment)
            }
        } else {
            null
        }
    }

    override fun round(price: Double): Double? {
        var result = price
        val step = getStep(result, true)
        return if (step != null) {
            val rest = Precision.round(result % step, 6)
            val complement = step - rest
            if (rest >= complement) {
                result += complement
            } else {
                result -= rest
            }
            Price.round(result)
        } else {
            null
        }
    }

    override val requiredCapabilities get() = setOf(PriceShiftFixedStep)
}
