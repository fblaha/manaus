package cz.fb.manaus.ischia.validator

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@BackLoserBet
@LayLoserBet
@Component
object CloseStartValidator : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val openDate = event.market.event.openDate
        val seconds = Instant.now().until(openDate, ChronoUnit.SECONDS)
        return if (seconds > 30) ValidationResult.ACCEPT else ValidationResult.REJECT
    }
}
