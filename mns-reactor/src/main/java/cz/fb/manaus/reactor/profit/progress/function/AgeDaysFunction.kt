package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

@Component
class AgeDaysFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double {
        val openDate = bet.market.event.openDate
        val days = DAYS.between(openDate, Instant.now())
        return days.toDouble()
    }
}
