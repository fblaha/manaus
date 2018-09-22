package cz.fb.manaus.reactor.price

import com.google.common.base.Preconditions
import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.PriceComparator
import cz.fb.manaus.core.model.TradedVolume
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PriceBulldozer {

    @Autowired
    private lateinit var roundingService: RoundingService

    fun bulldoze(threshold: Double, prices: List<Price>): List<Price> {
        var sum = 0.0
        Preconditions.checkState(Comparators.isInStrictOrder(prices, PriceComparator.CMP))
        val convicts = LinkedList<Price>()
        val untouched = LinkedList<Price>()
        for (price in prices) {
            if (sum >= threshold) {
                untouched.add(price)
            } else {
                convicts.add(price)
            }
            sum += price.amount
        }
        val priceMean = TradedVolume.getWeightedMean(convicts)
        if (priceMean.isPresent) {
            val amount = convicts.map { it.amount }.sum()
            val price = roundingService.roundBet(priceMean.asDouble)
            untouched.addFirst(Price(price!!, amount, prices[0].side))
        }
        return untouched
    }

}
