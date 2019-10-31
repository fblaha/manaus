package cz.fb.manaus.manila.validator

import cz.fb.manaus.manila.ManilaBet
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@ManilaBet
@Component
object CloseStartValidator : Validator {

    override fun validate(event: BetEvent): ValidationResult {
        val openDate = event.market.event.openDate
        val seconds = Instant.now().until(openDate, ChronoUnit.SECONDS)
        return if (seconds > 30) ValidationResult.ACCEPT else ValidationResult.REJECT
    }

}
