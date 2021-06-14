package cz.fb.manaus.core.model

import org.apache.commons.math3.stat.descriptive.moment.Mean

fun <T> getWeightedMean(data: List<T>, valFunc: (T) -> Double, weightFunc: (T) -> Double): Double? {
    return if (data.isEmpty()) {
        null
    } else {
        val values = data.map(valFunc).toDoubleArray()
        val weights = data.map(weightFunc).toDoubleArray()
        Mean().evaluate(values, weights)
    }
}


fun getWeightedMean(prices: List<Price>): Double? {
    return getWeightedMean(prices, Price::price, Price::amount)
}
