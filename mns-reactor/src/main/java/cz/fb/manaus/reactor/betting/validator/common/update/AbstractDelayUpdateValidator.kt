package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

abstract class AbstractDelayUpdateValidator(private val pausePeriod: Duration) : Validator {
    @Autowired
    private lateinit var actionDao: BetActionDao

    override val isUpdateOnly: Boolean = true

    override fun validate(context: BetContext): ValidationResult {
        val betId = context.oldBet!!.betId
        val actionDate = actionDao.getBetActionDate(betId).get()
        val untilNow = actionDate.toInstant().until(Instant.now(), ChronoUnit.MILLIS)
        return ValidationResult.of(untilNow > pausePeriod.toMillis())
    }

}
