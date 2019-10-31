package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.spring.ManausProfiles.DB
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class TheAbstractDelayUpdateValidatorTest : AbstractDatabaseTestCase() {
    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory

    private fun checkValidation(actionType: BetActionType, beforeMinutes: Long, lay: Side, validationResult: ValidationResult) {
        marketRepository.saveOrUpdate(market)
        val now = Instant.now()
        val place = betAction.copy(
                time = now.minus(beforeMinutes, ChronoUnit.MINUTES),
                price = Price(2.0, 30.0, lay),
                betActionType = actionType)
        betActionRepository.idSafeSave(place)
        val result = validator.validate(reactorTestFactory.newUpdateBetContext(runnerPrices, lay))
        assertEquals(validationResult, result)
    }

    @Test(expected = NullPointerException::class)
    fun `no bet action`() {
        marketRepository.saveOrUpdate(market)
        val result = validator.validate(reactorTestFactory.newUpdateBetContext(runnerPrices, Side.LAY))
        assertEquals(ValidationResult.REJECT, result)
    }

    @Test
    fun `close place`() {
        checkValidation(BetActionType.PLACE, 29, Side.LAY, ValidationResult.NOP)
    }

    @Test
    fun `far place`() {
        checkValidation(BetActionType.PLACE, 31, Side.LAY, ValidationResult.ACCEPT)
    }


    @Test
    fun `early update`() {
        checkValidation(BetActionType.UPDATE, 29, Side.LAY, ValidationResult.NOP)
    }

    @Test
    fun `far place back`() {
        checkValidation(BetActionType.PLACE, 31, Side.BACK, ValidationResult.ACCEPT)
    }

    @Test
    fun `close update back`() {
        checkValidation(BetActionType.UPDATE, 15, Side.BACK, ValidationResult.NOP)
    }

    @Test
    fun `far update`() {
        checkValidation(BetActionType.UPDATE, 150, Side.BACK, ValidationResult.ACCEPT)
    }

    @Component
    @Profile(DB)
    private class TestValidator : AbstractDelayUpdateValidator(Duration.ofMinutes(30))

}
