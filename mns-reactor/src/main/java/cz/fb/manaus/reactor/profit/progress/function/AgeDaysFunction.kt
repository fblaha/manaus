package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

@Component
class AgeDaysFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double {
        val openDate = bet.betAction.market.event.openDate.toInstant()
        val days = DAYS.between(openDate, Instant.now())
        return days.toDouble()
    }
}
