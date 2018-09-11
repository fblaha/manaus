package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import java.util.*

@Component
class OverroundBackFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        return bet.betAction.marketPrices.getOverround(Side.BACK)
    }

}
