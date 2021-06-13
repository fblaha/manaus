package cz.fb.manaus.reactor.price

import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.getWeightedMean
import org.springframework.stereotype.Component


fun getWeightedMean(prices: List<Price>): Double? {
    return getWeightedMean(prices, Price::price, Price::amount)
}

@Component
object PriceBulldozer {

    fun bulldoze(threshold: Double, prices: List<Price>): List<Price> {
        var sum = 0.0
        check(Comparators.isInStrictOrder(prices, PriceComparator))
        val convicts = mutableListOf<Price>()
        val untouched = mutableListOf<Price>()
        for (price in prices) {
            if (sum >= threshold) {
                untouched.add(price)
            } else {
                convicts.add(price)
            }
            sum += price.amount
        }
        val priceMean = getWeightedMean(convicts)
        if (priceMean != null) {
            val amount = convicts.sumOf { it.amount }
            val price = Price.round(priceMean)
            untouched.add(0, Price(price, amount, prices[0].side))
        }
        return untouched
    }

}
