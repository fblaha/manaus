package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals


class TheAbstractFairnessProposerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var layProposer: TestLayProposer
    @Autowired
    private lateinit var backProposer: TestBackProposer
    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun `lay price by lay proposer`() {
        val ctx = factory.createContext(Side.LAY, 2.5, 3.2)
        val proposedPrice = layProposer.getProposedPrice(ctx)
        assertEquals(2.96, Price.round(proposedPrice.asDouble))
    }

    @Test
    fun `back price by lay proposer`() {
        val ctx = factory.createContext(Side.BACK, 2.5, 3.5)
        val proposedPrice = layProposer.getProposedPrice(ctx)
        assertEquals(3.041, Price.round(proposedPrice.asDouble))
    }

    @Test
    fun `back price by back proposer`() {
        val ctx = factory.createContext(Side.BACK, 2.8, 3.5)
        val proposedPrice = backProposer.getProposedPrice(ctx)
        assertEquals(3.041, Price.round(proposedPrice.asDouble))
    }

    @Test
    fun `lay price by back proposer`() {
        val ctx = factory.createContext(Side.LAY, 2.2, 3.7)
        val proposedPrice = backProposer.getProposedPrice(ctx)
        assertEquals(2.96, Price.round(proposedPrice.asDouble))
    }

    @Component
    private class TestLayProposer : AbstractFairnessProposer(Side.LAY, { 0.02 })

    @Component
    private class TestBackProposer : AbstractFairnessProposer(Side.BACK, { 0.02 })


}