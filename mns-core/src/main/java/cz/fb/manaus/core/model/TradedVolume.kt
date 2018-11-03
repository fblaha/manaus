package cz.fb.manaus.core.model

import com.google.common.primitives.Doubles
import org.apache.commons.math3.stat.descriptive.moment.Mean


data class TradedVolume(val volume: List<Price>) {

    val weightedMean: Double?
        get() = getWeightedMean(volume)

    companion object {
        val EMPTY = TradedVolume(emptyList())
    }
}


fun getWeightedMean(volume: List<Price>): Double? {
    return if (volume.isEmpty()) {
        null
    } else {
        val prices = volume.map { it.price }
        val amounts = volume.map { it.amount }
        Mean().evaluate(Doubles.toArray(prices), Doubles.toArray(amounts))
    }
}
