package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class MatchedDelayFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val placed = bet.placedOrActionDate.toInstant()
        if (bet.matched != null) {
            val matched = bet.matched.toInstant()
            return placed.until(matched, ChronoUnit.MINUTES).toDouble()
        }
        return null
    }

}
