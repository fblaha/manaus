package cz.fb.manaus.core.model

import com.google.common.primitives.Doubles
import org.apache.commons.math3.stat.descriptive.moment.Mean
import java.util.*


data class TradedVolume(val volume: List<Price>) {

    val weightedMean: OptionalDouble
        get() = getWeightedMean(volume)

    companion object {
        val EMPTY = TradedVolume(emptyList())

        fun getWeightedMean(volume: List<Price>): OptionalDouble {
            return if (volume.isEmpty()) {
                OptionalDouble.empty()
            } else {
                val prices = volume.map { it.price }
                val amounts = volume.map { it.amount }
                OptionalDouble.of(Mean().evaluate(Doubles.toArray(prices), Doubles.toArray(amounts)))
            }
        }
    }
}
