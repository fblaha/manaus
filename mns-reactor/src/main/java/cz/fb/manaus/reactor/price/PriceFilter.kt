package cz.fb.manaus.reactor.price


import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.Side

class PriceFilter(private val limit: Int,
                  private val bulldozeThreshold: Double,
                  private val priceRange: ClosedRange<Double>,
                  private val bulldozer: PriceBulldozer) {

    internal fun getSignificantPrices(limit: Int, prices: List<Price>): List<Price> {
        val (back, lay) = prices.filter { it.price in this.priceRange }.partition { it.side == Side.BACK }
        return filter(limit, back) + filter(limit, lay)
    }

    private fun filter(limit: Int, prices: List<Price>): List<Price> {
        val sorted = prices.sortedWith(PriceComparator)
        return bulldozer.bulldoze(bulldozeThreshold, sorted).take(limit)
    }

    fun filter(prices: List<Price>): List<Price> {
        return getSignificantPrices(limit, prices)
    }
}
