package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class LastMatchedReciprocalFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val reciprocal = bet.betAction.marketPrices.lastMatchedReciprocal
        return if (reciprocal.isPresent) reciprocal.asDouble else null
    }

}
