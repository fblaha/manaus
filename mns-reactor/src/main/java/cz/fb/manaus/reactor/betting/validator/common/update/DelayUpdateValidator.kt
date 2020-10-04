package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.UpdateOnlyValidator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class DelayUpdateValidator(
        private val pausePeriod: Duration,
        private val betActionRepository: BetActionRepository
) : UpdateOnlyValidator {

    override fun validate(event: BetEvent): ValidationResult {
        val betId = event.oldBet!!.betId!!
        val action = betActionRepository.findRecentBetAction(betId) ?: error("no such action")
        val untilNow = action.time.until(Instant.now(), ChronoUnit.MILLIS)
        return if (untilNow > pausePeriod.toMillis()) ValidationResult.OK else ValidationResult.NOP
    }
}
