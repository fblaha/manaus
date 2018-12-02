package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class PriceLayFunction : ProgressFunction {

    override val includeNoValues: Boolean get() = false

    override fun invoke(bet: RealizedBet): Double? {
        val price = bet.settledBet.price
        return when {
            price.side == Side.LAY -> price.price
            else -> null
        }
    }

}
