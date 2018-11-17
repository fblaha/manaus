package cz.fb.manaus.reactor.betting.validator.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homePrices
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.spring.ManausProfiles.Companion.TEST
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles(value = ["matchbook", TEST], inheritProfiles = false)
class TheAbstractLastMatchedValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `accept lay`() {
        checkValidator(Side.LAY, ValidationResult.ACCEPT, ValidationResult.REJECT)
    }

    @Test
    fun `accept back`() {
        checkValidator(Side.BACK, ValidationResult.REJECT, ValidationResult.ACCEPT)
    }

    private fun checkValidator(side: Side, lowerResult: ValidationResult, higherResult: ValidationResult) {
        val context = factory.newBetContext(side, listOf(homePrices.copy(lastMatchedPrice = 2.1)), null)
        context.newPrice = Price(2.0, 2.0, side)
        assertEquals(lowerResult, validator.validate(context))
        context.newPrice = Price(2.2, 2.0, side)
        assertEquals(higherResult, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractLastMatchedValidator(true)
}

