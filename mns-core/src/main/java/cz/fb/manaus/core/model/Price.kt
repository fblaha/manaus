package cz.fb.manaus.core.model

import org.apache.commons.math3.util.Precision


data class Price(
        val price: Double,
        val amount: Double,
        val side: Side
) {
    companion object {
        fun round(value: Double): Double {
            return Precision.round(value, 3)
        }

        fun priceEq(first: Double, second: Double): Boolean {
            return Precision.equals(first, second, 0.0001)
        }

        fun amountEq(first: Double, second: Double): Boolean {
            return Precision.equals(first, second, 0.0001)
        }
    }
}

