package cz.fb.manaus.reactor.betting.validator.common.update

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
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
        val oldBet = Optional.of<Bet>(ReactorTestFactory.newBet(oldPrice))

        val prices = factory.createMarket(0.1, listOf(0.4, 0.3, 0.3))
        val runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME)
        assertEquals(ValidationResult.REJECT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(oldPrice)))

        assertEquals(ValidationResult.REJECT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(roundingService.decrement(oldPrice, 1).get())))

        assertEquals(ValidationResult.ACCEPT, validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(roundingService.decrement(oldPrice, 3).get())))
    }

    @Test
    fun `accept lay`() {
        val newOne = mock<Price>()
        val oldOne = mock<Price>()
        whenever(newOne.side).thenReturn(Side.LAY)
        whenever(oldOne.side).thenReturn(Side.LAY)
        whenever(oldOne.price).thenReturn(3.6)
        val oldBet = Optional.of<Bet>(ReactorTestFactory.newBet(oldOne))

        val prices = factory.createMarket(0.1, listOf(0.4, 0.3, 0.3))
        val runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME)

        val context = factory.newBetContext(Side.LAY, prices, runnerPrices, oldBet).withNewPrice(newOne)
        whenever(newOne.price).thenReturn(3.65)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.7)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.75)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))

        whenever(newOne.price).thenReturn(3.55)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.5)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractTooCloseUpdateEpsilonValidator(0.02)

}