package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.HOME_EVENT
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
        val context = HOME_EVENT.copy(side = Side.LAY,
                marketPrices = factory.newMarketPrices(2.0, 4.5))
        assertEquals(ValidationResult.OK, layProposer.validate(context))
        assertEquals(2.02, layProposer.getProposedPrice(context))
    }

    @Test
    fun check() {
        val context = HOME_EVENT.copy(side = Side.LAY)
        assertEquals(ValidationResult.OK, layProposer.validate(context))
        assertEquals(ValidationResult.OK, backProposer.validate(context.copy(side = Side.BACK)))
    }

    @Test
    fun `back propose`() {
        val prices = factory.newMarketPrices(2.5, 3.5)
        val context = HOME_EVENT.copy(marketPrices = prices)
        assertEquals(ValidationResult.OK, backProposer.validate(context))
        assertEquals(3.45, backProposer.getProposedPrice(context))
    }

    @Component
    private class LayProposer : AbstractBestPriceProposer(1)

    @Component
    private class BackProposer : AbstractBestPriceProposer(1)

}
