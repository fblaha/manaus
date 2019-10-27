package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

abstract class AbstractDelayUpdateValidator(private val pausePeriod: Duration) : Validator {
    @Autowired
    private lateinit var betActionRepository: BetActionRepository

    override val isUpdateOnly: Boolean = true

    override fun validate(event: BetEvent): ValidationResult {
        val betId = event.oldBet!!.betId!!
        val actionDate = betActionRepository.findRecentBetAction(betId)!!.time
        val untilNow = actionDate.until(Instant.now(), ChronoUnit.MILLIS)
        return ValidationResult.of(untilNow > pausePeriod.toMillis())
    }

}
