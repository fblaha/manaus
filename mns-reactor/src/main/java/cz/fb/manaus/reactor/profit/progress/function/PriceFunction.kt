package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class PriceFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        return OptionalDouble.of(bet.price.price)
    }

}
