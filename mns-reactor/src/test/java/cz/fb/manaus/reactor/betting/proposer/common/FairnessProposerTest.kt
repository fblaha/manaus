package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.betting.proposer.FixedDowngradeStrategy
import cz.fb.manaus.reactor.betting.proposer.PriceProposer
import cz.fb.manaus.reactor.price.PriceService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals


class FairnessProposerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var layProposer: TestLayProposer

    @Autowired
    private lateinit var backProposer: TestBackProposer

    @Autowired
    private lateinit var factory: BetEventTestFactory

    @Test
    fun `lay price by lay proposer`() {
        val event = factory.newBetEvent(Side.LAY, 2.5, 3.2)
        val proposedPrice = layProposer.getProposedPrice(event)!!
        assertEquals(2.96, Price.round(proposedPrice))
    }

    @Test
    fun `back price by lay proposer`() {
        val event = factory.newBetEvent(Side.BACK, 2.5, 3.5)
        val proposedPrice = layProposer.getProposedPrice(event)!!
        assertEquals(3.041, Price.round(proposedPrice))
    }

    @Test
    fun `back price by back proposer`() {
        val event = factory.newBetEvent(Side.BACK, 2.8, 3.5)
        val proposedPrice = backProposer.getProposedPrice(event)!!
        assertEquals(3.041, Price.round(proposedPrice))
    }

    @Test
    fun `lay price by back proposer`() {
        val event = factory.newBetEvent(Side.LAY, 2.2, 3.7)
        val proposedPrice = backProposer.getProposedPrice(event)!!
        assertEquals(2.96, Price.round(proposedPrice))
    }


    @Component
    class TestLayProposer(priceService: PriceService)
        : PriceProposer by FairnessProposer(
            Side.LAY, priceService,
            FixedDowngradeStrategy(Side.LAY, 0.02),
            FixedDowngradeStrategy(Side.BACK, 0.02)
    )

    @Component
    class TestBackProposer(priceService: PriceService)
        : PriceProposer by FairnessProposer(
            Side.BACK,
            priceService,
            FixedDowngradeStrategy(Side.BACK, 0.02),
            FixedDowngradeStrategy(Side.LAY, 0.02)
    )

}

