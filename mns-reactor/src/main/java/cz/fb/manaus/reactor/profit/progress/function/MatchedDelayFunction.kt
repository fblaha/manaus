package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class MatchedDelayFunction : ProgressFunction {

    override fun apply(bet: SettledBet): OptionalDouble {
        val placed = bet.placedOrActionDate.toInstant()
        if (bet.matched != null) {
            val matched = bet.matched.toInstant()
            return OptionalDouble.of(placed.until(matched, ChronoUnit.MINUTES).toDouble())
        }
        return OptionalDouble.empty()
    }

}
