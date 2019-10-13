package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Service

@Service
class RoundingService(private val plugin: RoundingPlugin) {


    fun increment(price: Double, stepNum: Int): Double? {
        val result = plugin.shift(price, stepNum)
        if (result != null) check(result > price)
        return result
    }

    fun decrement(price: Double, stepNum: Int, minPrice: Double): Double? {
        val result = plugin.shift(price, -stepNum)
        if (result != null) {
            check(result < price)
            if (result < minPrice) {
                return null
            }
        }
        return result
    }

    fun downgrade(price: Double, stepNum: Int, side: Side, minPrice: Double): Double? {
        if (side === Side.LAY) {
            return decrement(price, stepNum, minPrice)
        } else if (side === Side.BACK) {
            return increment(price, stepNum)
        }
        throw IllegalStateException()
    }

    fun increment(price: Price, stepNum: Int): Price? {
        val newPrice = increment(price.price, stepNum)
        return if (newPrice != null) {
            Price(newPrice, price.amount, price.side)
        } else {
            null
        }
    }

    fun decrement(price: Price, stepNum: Int, minPrice: Double): Price? {
        val newPrice = decrement(price.price, stepNum, minPrice)
        return if (newPrice != null) {
            Price(newPrice, price.amount, price.side)
        } else {
            null
        }
    }

    fun roundBet(price: Double): Double? {
        return plugin.round(price)
    }

}
