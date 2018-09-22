package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

interface AheadTimeFunction : ProgressFunction {

    override fun invoke(bet: SettledBet): Double? {
        val eventTime = getRelatedTime(bet)
        return if (eventTime.isPresent) {
            val openDate = bet.betAction.market.event.openDate.toInstant()
            val minutes = eventTime.get().until(openDate, ChronoUnit.MINUTES)
            minutes / 60.0
        } else {
            null
        }
    }

    fun getRelatedTime(bet: SettledBet): Optional<Instant>

}
