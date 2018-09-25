package cz.fb.manaus.reactor.betting.validator.common

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.spring.ManausProfiles.TEST
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import java.util.*
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
        val runnerPrices = mock<RunnerPrices>()
        whenever(runnerPrices.lastMatchedPrice).thenReturn(2.1)
        whenever(runnerPrices.selectionId).thenReturn(CoreTestFactory.HOME)
        val marketPrices = mock<MarketPrices>()
        whenever(marketPrices.getRunnerPrices(ArgumentMatchers.anyLong())).thenReturn(runnerPrices)
        whenever(marketPrices.getReciprocal(Side.BACK)).thenReturn(OptionalDouble.of(0.9))
        assertEquals(lowerResult, validator.validate(factory.newBetContext(side, marketPrices, null)
                .withNewPrice(Price(2.0, 2.0, side))))
        assertEquals(higherResult, validator.validate(factory.newBetContext(side, marketPrices, null)
                .withNewPrice(Price(2.2, 2.0, side))))
    }

    @Component
    private class TestValidator : AbstractLastMatchedValidator(true)

}

