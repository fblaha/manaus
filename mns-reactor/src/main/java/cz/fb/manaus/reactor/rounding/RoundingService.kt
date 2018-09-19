package cz.fb.manaus.reactor.rounding

import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.Objects.requireNonNull

@Service
class RoundingService {

    @Autowired
    private lateinit var plugin: RoundingPlugin
    @Autowired
    private lateinit var provider: ExchangeProvider

    fun increment(price: Double, stepNum: Int): OptionalDouble {
        val result = plugin.shift(price, stepNum)
        if (result.isPresent) checkState(result.asDouble > price)
        return result
    }

    fun decrement(price: Double, stepNum: Int): OptionalDouble {
        val result = plugin.shift(price, -stepNum)
        if (result.isPresent) {
            checkState(result.asDouble < price)
            if (result.asDouble < provider.minPrice) {
                return OptionalDouble.empty()
            }
        }
        return result
    }

    fun downgrade(price: Double, stepNum: Int, side: Side): OptionalDouble {
        if (requireNonNull(side) === Side.LAY) {
            return decrement(price, stepNum)
        } else if (side === Side.BACK) {
            return increment(price, stepNum)
        }
        throw IllegalStateException()
    }

    fun increment(price: Price, stepNum: Int): Optional<Price> {
        val newPrice = increment(price.price, stepNum)
        return if (newPrice.isPresent) {
            Optional.of(Price(newPrice.asDouble, price.amount, price.side))
        } else {
            Optional.empty()
        }
    }

    fun decrement(price: Price, stepNum: Int): Optional<Price> {
        val newPrice = decrement(price.price, stepNum)
        return if (newPrice.isPresent) {
            Optional.of(Price(newPrice.asDouble, price.amount, price.side))
        } else {
            Optional.empty()
        }
    }

    fun roundBet(price: Double): OptionalDouble {
        return plugin.round(price)
    }

}
