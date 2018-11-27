package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component

@Component
class PriceBackFunction : ProgressFunction {

    override val includeNoValues: Boolean
        get() = false

    override fun invoke(bet: RealizedBet): Double? {
        val price = bet.settledBet.price
        return if (price.side == Side.BACK) price.price else null
    }

}
