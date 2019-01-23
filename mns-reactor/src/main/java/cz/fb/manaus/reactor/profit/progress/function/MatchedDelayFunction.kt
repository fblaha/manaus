package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
object MatchedDelayFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val placed = bet.settledBet.placed
        if (placed != null) {
            val matched = bet.settledBet.matched
            return placed.until(matched, ChronoUnit.MINUTES).toDouble()
        }
        return null
    }
}
