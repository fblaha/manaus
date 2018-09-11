package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class TimeDiffFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val placed = bet.placedOrActionDate.toInstant()
        val actionDate = bet.betAction.actionDate.toInstant()
        return OptionalDouble.of(actionDate.until(placed, ChronoUnit.SECONDS).toDouble())
    }

}
