package cz.fb.manaus.core.model

import com.google.common.primitives.Doubles
import org.apache.commons.math3.stat.descriptive.moment.Mean

data class TradedAmount(val price: Double, val amount: Double)

data class TradedVolume(val volume: List<TradedAmount>) {

    val weightedMean: Double?
        get() = getWeightedMean(volume)

    companion object {
        val EMPTY = TradedVolume(emptyList())
    }
}


fun getWeightedMean(volume: List<TradedAmount>): Double? {
    return if (volume.isEmpty()) {
        null
    } else {
        val prices = volume.map { it.price }
        val amounts = volume.map { it.amount }
        Mean().evaluate(Doubles.toArray(prices), Doubles.toArray(amounts))
    }
}
