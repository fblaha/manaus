package cz.fb.manaus.core.model

import org.apache.commons.math3.util.Precision
import kotlin.math.abs


data class Price(
    val price: Double,
    val amount: Double,
    val side: Side
) : Comparable<Price> {

    override fun compareTo(other: Price): Int {
        check(side == other.side) { "mixed sides" }
        return when (side) {
            Side.BACK -> compareValuesBy(this, other, Price::price, Price::amount)
            Side.LAY -> compareValuesBy(other, this, Price::price, Price::amount)
        }
    }

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
