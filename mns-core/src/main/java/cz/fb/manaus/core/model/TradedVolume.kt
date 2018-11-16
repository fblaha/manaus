package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class TradedAmount(val price: Double, val amount: Double)

data class TradedVolume(val volume: List<TradedAmount>) {

    val weightedMean: Double?
        @JsonIgnore
        get() = getWeightedMean(volume)

    companion object {
        val EMPTY = TradedVolume(emptyList())
    }
}


fun getWeightedMean(volume: List<TradedAmount>): Double? {
    return getWeightedMean(volume, TradedAmount::price, TradedAmount::amount)
}
