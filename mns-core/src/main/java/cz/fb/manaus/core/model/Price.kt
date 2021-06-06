package cz.fb.manaus.core.model

import org.apache.commons.math3.util.Precision
import kotlin.math.abs


data class Price(
    val price: Double,
    val amount: Double,
    val side: Side
) {
    companion object {
        fun round(value: Double): Double {
            return Precision.round(value, 3)
        }
    }
}

infix fun Double.priceEq(second: Double): Boolean {
    return abs(this - second) < 0.0001
}

infix fun Double.amountEq(second: Double): Boolean {
    return abs(this - second) < 0.0001
}
