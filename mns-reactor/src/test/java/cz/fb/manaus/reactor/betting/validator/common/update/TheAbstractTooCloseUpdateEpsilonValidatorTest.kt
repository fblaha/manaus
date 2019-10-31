package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class TheAbstractTooCloseUpdateEpsilonValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: ReactorTestFactory


    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice)

        val prices = factory.newMarketPrices(0.1, listOf(0.4, 0.3, 0.3))
        val context = factory.newBetEvent(Side.BACK, prices, oldBet)
        context.newPrice = oldPrice
        assertEquals(ValidationResult.NOP, validator.validate(context))


        context.newPrice = roundingService.decrement(oldPrice, 1, provider.minPrice, provider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 3, provider.minPrice, provider::matches)
        assertEquals(ValidationResult.OK, validator.validate(context))
    }

    @Test
    fun `accept lay`() {
        val newOne = Price(3.65, 3.0, Side.LAY)
        val oldOne = Price(3.6, 3.0, Side.LAY)
        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val context = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        context.newPrice = newOne.copy(price = 3.65)
        assertEquals(ValidationResult.NOP, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.7)
        assertEquals(ValidationResult.OK, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.75)
        assertEquals(ValidationResult.OK, validator.validate(context))

        context.newPrice = newOne.copy(price = 3.55)
        assertEquals(ValidationResult.NOP, validator.validate(context))
        context.newPrice = newOne.copy(price = 3.5)
        assertEquals(ValidationResult.OK, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractTooCloseUpdateEpsilonValidator(0.02)

}