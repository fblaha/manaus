package cz.fb.manaus.reactor.betting.validator.common.update

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

class TheAbstractTooCloseUpdateValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var factory: ReactorTestFactory
    @Autowired
    private lateinit var provider: ExchangeProvider

    private lateinit var prices: List<RunnerPrices>
    private lateinit var runnerPrices: RunnerPrices

    @Before
    fun setUp() {
        prices = factory.createMarketPrices(0.1, listOf(0.4, 0.3, 0.3))
        runnerPrices = getRunnerPrices(prices, SEL_HOME)
    }

    @Test
    fun `accept back`() {
        val oldPrice = Price(2.5, 5.0, Side.BACK)
        val oldBet = betTemplate.copy(requestedPrice = oldPrice)

        val context = factory.newBetContext(Side.BACK, prices, oldBet)
        context.newPrice = oldPrice
        assertEquals(ValidationResult.REJECT, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 1)
        assertEquals(ValidationResult.REJECT, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 2)
        assertEquals(ValidationResult.REJECT, validator.validate(context))

        context.newPrice = roundingService.decrement(oldPrice, 3)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Test
    fun `accept lay`() {
        val newOne = mock<Price>()
        val oldOne = mock<Price>()
        whenever(newOne.side).thenReturn(Side.LAY)
        whenever(oldOne.side).thenReturn(Side.LAY)
        whenever(newOne.price).thenReturn(3.15)
        whenever(oldOne.price).thenReturn(3.1)
        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val context = factory.newBetContext(Side.LAY, prices, oldBet)
        context.newPrice = newOne
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.2)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.05)
        assertEquals(ValidationResult.REJECT, validator.validate(context))
        whenever(newOne.price).thenReturn(3.25)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Test
    fun `minimal price`() {
        val newOne = mock<Price>()
        val oldOne = mock<Price>()
        whenever(newOne.side).thenReturn(Side.LAY)
        whenever(oldOne.side).thenReturn(Side.LAY)
        whenever(oldOne.price).thenReturn(provider.minPrice)
        whenever(newOne.price).thenReturn(1.04)
        val oldBet = betTemplate.copy(requestedPrice = oldOne)

        val context = factory.newBetContext(Side.LAY, prices, oldBet)
        context.newPrice = newOne
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

    @Component
    private class TestValidator : AbstractTooCloseUpdateValidator(setOf(-2, -1, 1, 2))

}