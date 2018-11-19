package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class TimeDiffFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val placed = bet.settledBet.placed
        val actionDate = bet.betAction.time
        return when {
            placed != null -> actionDate.until(placed, ChronoUnit.SECONDS).toDouble()
            else -> null
        }
    }

}
