package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.rounding.RoundingService
import cz.fb.manaus.reactor.rounding.decrement
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class TooCloseUpdateValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: BetEventTestFactory


    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice)

        val event = factory.newBetEvent(Side.BACK, runnerPrices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = oldPrice)))

        var newPrice = roundingService.decrement(oldPrice, 1, bfProvider.minPrice, bfProvider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newPrice)))

        newPrice = roundingService.decrement(oldPrice, 2, bfProvider.minPrice, bfProvider::matches)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newPrice)))

        newPrice = roundingService.decrement(oldPrice, 3, bfProvider.minPrice, bfProvider::matches)
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newPrice)))
    }

    @Test
    fun `accept lay`() {
        val newOne = Price(3.15, 3.0, Side.LAY)
        val oldOne = Price(3.1, 3.0, Side.LAY)

        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val event = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne)))
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.2))))
        assertEquals(ValidationResult.NOP, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.05))))
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne.copy(price = 3.25))))
    }

    @Test
    fun `minimal price`() {
        val newOne = Price(1.04, 5.0, Side.LAY)
        val oldOne = Price(bfProvider.minPrice, 5.0, Side.LAY)
        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val event = factory.newBetEvent(Side.LAY, runnerPrices, oldBet)
        assertEquals(ValidationResult.OK, validator.validate(event.copy(proposedPrice = newOne)))
    }

    @Component
    class TestValidator(roundingService: RoundingService)
        : Validator by TooCloseUpdateValidator(
            setOf(-2, -1, 1, 2),
            roundingService
    )

}