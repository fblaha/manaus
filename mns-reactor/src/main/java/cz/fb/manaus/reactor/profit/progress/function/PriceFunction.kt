package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class PriceFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        return bet.price.price
    }

}