package cz.fb.manaus.reactor.rounding

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.stereotype.Service

@Service
class RoundingService(private val plugin: RoundingPlugin,
                      private val provider: ExchangeProvider) {


    fun increment(price: Double, stepNum: Int): Double? {
        val result = plugin.shift(price, stepNum)
        if (result != null) checkState(result > price)
        return result
    }

    fun decrement(price: Double, stepNum: Int): Double? {
        val result = plugin.shift(price, -stepNum)
        if (result != null) {
            checkState(result < price)
            if (result < provider.minPrice) {
                return null
            }
        }
        return result
    }

    fun downgrade(price: Double, stepNum: Int, side: Side): Double? {
        if (side === Side.LAY) {
            return decrement(price, stepNum)
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

    fun decrement(price: Price, stepNum: Int): Price? {
        val newPrice = decrement(price.price, stepNum)
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
