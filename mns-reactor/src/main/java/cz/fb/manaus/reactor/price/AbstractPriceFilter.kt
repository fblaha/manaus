package cz.fb.manaus.reactor.price


import com.google.common.collect.Range
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.Side
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractPriceFilter(private val minCount: Int,
                                   private val bulldozeThreshold: Double,
                                   private val priceRange: Range<Double>) {
    @Autowired
    private lateinit var bulldozer: PriceBulldozer

    internal fun getSignificantPrices(minCount: Int, prices: List<Price>): List<Price> {
        val bySide = prices.filter { this.priceRangeFilter(it) }.groupBy { it.side }
        val sortedBack = PriceComparator.ORDERING.immutableSortedCopy(
                bySide.getOrDefault(Side.BACK, emptyList()))
        val sortedLay = PriceComparator.ORDERING.immutableSortedCopy(
                bySide.getOrDefault(Side.LAY, emptyList()))
        val bulldozedBack = bulldozer.bulldoze(bulldozeThreshold, sortedBack)
        val bulldozedLay = bulldozer.bulldoze(bulldozeThreshold, sortedLay)
        val topBack = bulldozedBack.take(minCount)
        val topLay = bulldozedLay.take(minCount)
        return topBack + topLay
    }

    private fun priceRangeFilter(price: Price): Boolean {
        return priceRange.contains(price.price)
    }

    fun filter(prices: List<Price>): List<Price> {
        return getSignificantPrices(minCount, prices)
    }
}
