package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import java.time.Instant
import java.time.temporal.ChronoUnit

interface AheadTimeFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        val eventTime = getRelatedTime(bet)
        return if (eventTime != null) {
            val openDate = bet.market.event.openDate
            val minutes = eventTime.until(openDate, ChronoUnit.MINUTES)
            minutes / 60.0
        } else {
            null
        }
    }

    fun getRelatedTime(bet: RealizedBet): Instant?

}
