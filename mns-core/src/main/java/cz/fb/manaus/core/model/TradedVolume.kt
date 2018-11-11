package cz.fb.manaus.core.model

data class TradedAmount(val price: Double, val amount: Double)

data class TradedVolume(val volume: List<TradedAmount>) {

    val weightedMean: Double?
        get() = getWeightedMean(volume)

    companion object {
        val EMPTY = TradedVolume(emptyList())
    }
}


fun getWeightedMean(volume: List<TradedAmount>): Double? {
    return getWeightedMean(volume, TradedAmount::price, TradedAmount::amount)
}
