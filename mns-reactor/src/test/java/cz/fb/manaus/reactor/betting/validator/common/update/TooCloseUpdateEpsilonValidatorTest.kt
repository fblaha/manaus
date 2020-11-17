package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class TooCloseUpdateEpsilonValidatorTest : AbstractTestCase() {

    @Autowired
    private lateinit var validator: TestValidator

    @Autowired
    private lateinit var roundingService: RoundingService

    @Autowired
    private lateinit var pricesTestFactory: PricesTestFactory

    @Autowired
    private lateinit var factory: BetEventTestFactory


    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice).asTracked

        val prices = pricesTestFactory.newMarketPrices(0.1, listOf(0.4, 0.3, 0.3))
        val event = factory.newBetEvent(Side.BACK, prices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = oldPrice)))

        var newPrice = roundingService.decrement(oldPrice, 1, bfProvider.minPrice, bfProvider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newPrice)))

        newPrice = roundingService.decrement(oldPrice, 3, bfProvider.minPrice, bfProvider::matches)
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newPrice)))
    }

    @Test
    fun `accept lay`() {
        val newOne = Price(3.65, 3.0, Side.LAY)
        val oldOne = Price(3.6, 3.0, Side.LAY)
        val oldBet = betTemplate.copy(requestedPrice = oldOne).asTracked

        val event = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.65))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.7))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.75))))

        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.55))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.5))))
    }

    @Component
    private class TestValidator : Validator by TooCloseUpdateEpsilonValidator(0.02)

}