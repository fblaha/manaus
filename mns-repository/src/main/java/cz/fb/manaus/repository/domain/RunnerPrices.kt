package cz.fb.manaus.repository.domain

data class RunnerPrices(
        val selectionId: Long,
        val prices: List<Price>,
        val lastMatchedPrice: Double?,
        val matchedAmount: Double?
) {
    fun getHomogeneous(side: Side): RunnerPrices {
        return this.copy(prices = this.prices.filter { price -> price.side === side })
    }

    val bestPrice: Price?
        get() = prices.minWith(PriceComparator)

    val sortedPrices: List<Price>
        get() = prices.sortedWith(PriceComparator)

}