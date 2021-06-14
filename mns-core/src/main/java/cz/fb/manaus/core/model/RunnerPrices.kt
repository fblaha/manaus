package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class RunnerPrices(
    val selectionId: Long,
    val prices: List<Price>,
    val lastMatchedPrice: Double?,
    val matchedAmount: Double?
) {
    fun getHomogeneous(side: Side): RunnerPrices {
        return this.copy(prices = this.prices.filter { it.side == side })
    }

    val bestPrice: Price?
        @JsonIgnore
        get() = prices.maxOrNull()

}