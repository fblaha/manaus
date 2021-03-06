package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TooCloseUpdateEpsilonValidatorTest {

    private val validator = TooCloseUpdateEpsilonValidator(0.02)

    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice).asTracked

        val prices = PricesTestFactory.newMarketPrices(0.1, listOf(0.4, 0.3, 0.3))
        val event = BetEventTestFactory.newBetEvent(Side.BACK, prices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = oldPrice)))

        var newPrice = oldPrice.copy(price = oldPrice.price * 0.99)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newPrice)))

        newPrice = oldPrice.copy(price = oldPrice.price * 0.97)
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newPrice)))
    }

    @Test
    fun `accept lay`() {
        val newOne = Price(3.65, 3.0, Side.LAY)
        val oldOne = Price(3.6, 3.0, Side.LAY)
        val oldBet = betTemplate.copy(requestedPrice = oldOne).asTracked

        val event = BetEventTestFactory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.65))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.7))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.75))))

        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.55))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.5))))
    }

}