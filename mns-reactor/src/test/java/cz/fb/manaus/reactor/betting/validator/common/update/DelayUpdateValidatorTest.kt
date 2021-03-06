package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.spring.ManausProfiles.DB
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DelayUpdateValidatorTest : AbstractIntegrationTestCase() {
    @Autowired
    private lateinit var validator: TestValidator

    private fun checkValidation(
            actionType: BetActionType,
            beforeMinutes: Long,
            side: Side,
            validationResult: ValidationResult
    ) {
        marketRepository.save(market)
        val now = Instant.now()
        val place = betAction.copy(
                time = now.minus(beforeMinutes, ChronoUnit.MINUTES),
                price = Price(2.0, 30.0, side),
                betActionType = actionType
        )
        betActionRepository.save(place)
        val result = validator.validate(BetEventTestFactory.newUpdateBetEvent(side, runnerPrices))
        assertEquals(validationResult, result)
    }

    @Test
    fun `no bet action`() {
        assertFailsWith<IllegalStateException> {
            marketRepository.save(market)
            validator.validate(BetEventTestFactory.newUpdateBetEvent(Side.LAY, runnerPrices))
        }
    }

    @Test
    fun `close place`() {
        checkValidation(BetActionType.PLACE, 29, Side.LAY, ValidationResult.NOP)
    }

    @Test
    fun `far place`() {
        checkValidation(BetActionType.PLACE, 31, Side.LAY, ValidationResult.OK)
    }


    @Test
    fun `early update`() {
        checkValidation(BetActionType.UPDATE, 29, Side.LAY, ValidationResult.NOP)
    }

    @Test
    fun `far place back`() {
        checkValidation(BetActionType.PLACE, 31, Side.BACK, ValidationResult.OK)
    }

    @Test
    fun `close update back`() {
        checkValidation(BetActionType.UPDATE, 15, Side.BACK, ValidationResult.NOP)
    }

    @Test
    fun `far update`() {
        checkValidation(BetActionType.UPDATE, 150, Side.BACK, ValidationResult.OK)
    }

    @Component
    @Profile(DB)
    class TestValidator(betActionRepository: BetActionRepository) :
            Validator by DelayUpdateValidator(Duration.ofMinutes(30), betActionRepository)

}
