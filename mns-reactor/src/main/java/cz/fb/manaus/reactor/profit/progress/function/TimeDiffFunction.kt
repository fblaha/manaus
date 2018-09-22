package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class TimeDiffFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        val placed = bet.placedOrActionDate.toInstant()
        val actionDate = bet.betAction.actionDate.toInstant()
        return actionDate.until(placed, ChronoUnit.SECONDS).toDouble()
    }

}
