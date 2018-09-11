package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.util.*

@Component
class LastMatchedReciprocalFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        return bet.betAction.marketPrices.lastMatchedReciprocal
    }

}
