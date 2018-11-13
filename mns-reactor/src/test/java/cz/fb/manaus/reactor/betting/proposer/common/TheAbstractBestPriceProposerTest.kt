package cz.fb.manaus.reactor.betting.proposer.common

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.SEL_HOME
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.homePrices
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals


class TheAbstractBestPriceProposerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var layProposer: LayProposer
    @Autowired
    private lateinit var backProposer: BackProposer
    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `lay propose`() {
        val context = mock<BetContext>()
        whenever(context.side).thenReturn(Side.LAY)
        whenever(context.runnerPrices).thenReturn(factory.newRunnerPrices(SEL_HOME, 2.0, 4.5))
        assertEquals(ValidationResult.ACCEPT, layProposer.validate(context))
        assertEquals(2.02, layProposer.getProposedPrice(context))
    }

    @Test
    fun check() {
        val context = mock<BetContext>()
        whenever(context.side).thenReturn(Side.LAY, Side.BACK)
        whenever(context.runnerPrices).thenReturn(homePrices)
        assertEquals(ValidationResult.ACCEPT, layProposer.validate(context))
        assertEquals(ValidationResult.ACCEPT, backProposer.validate(context))
    }

    @Test
    fun `back propose`() {
        val context = mock<BetContext>()
        whenever(context.side).thenReturn(Side.BACK)
        val prices = factory.newRunnerPrices(SEL_HOME, 2.5, 3.5)
        whenever(context.runnerPrices).thenReturn(prices)
        assertEquals(ValidationResult.ACCEPT, backProposer.validate(context))
        assertEquals(3.45, backProposer.getProposedPrice(context))
    }

    @Component
    private class LayProposer : AbstractBestPriceProposer(1)

    @Component
    private class BackProposer : AbstractBestPriceProposer(1)

}
