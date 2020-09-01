package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homePrices
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class LastMatchedValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator

    @Autowired
    private lateinit var factory: BetEventTestFactory

    @Test
    fun `accept lay`() {
        checkValidator(Side.LAY, ValidationResult.OK, ValidationResult.DROP)
    }

    @Test
    fun `accept back`() {
        checkValidator(Side.BACK, ValidationResult.DROP, ValidationResult.OK)
    }

    private fun checkValidator(side: Side, lowerResult: ValidationResult, higherResult: ValidationResult) {
        val event = factory.newBetEvent(side, listOf(homePrices.copy(lastMatchedPrice = 2.1)), null)
        assertEquals(lowerResult, validator.validate(event.copy(proposedPrice = Price(2.0, 2.0, side))))
        assertEquals(higherResult, validator.validate(event.copy(proposedPrice = Price(2.2, 2.0, side))))
    }

    @Component
    private class TestValidator : Validator by LastMatchedValidator(true)
}

