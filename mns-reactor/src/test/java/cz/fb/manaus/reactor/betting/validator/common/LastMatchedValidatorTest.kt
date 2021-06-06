package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homePrices
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LastMatchedValidatorTest {

    private val validator = LastMatchedValidator(true)

    @Test
    fun `accept lay`() {
        checkValidator(Side.LAY, ValidationResult.OK, ValidationResult.DROP)
    }

    @Test
    fun `accept back`() {
        checkValidator(Side.BACK, ValidationResult.DROP, ValidationResult.OK)
    }

    private fun checkValidator(side: Side, lowerResult: ValidationResult, higherResult: ValidationResult) {
        val event = BetEventTestFactory.newBetEvent(side, listOf(homePrices.copy(lastMatchedPrice = 2.1)), null)
        assertEquals(lowerResult, validator.validate(event.copy(proposedPrice = Price(2.0, 2.0, side))))
        assertEquals(higherResult, validator.validate(event.copy(proposedPrice = Price(2.2, 2.0, side))))
    }
}

