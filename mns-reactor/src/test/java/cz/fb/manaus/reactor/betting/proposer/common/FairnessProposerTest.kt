package cz.fb.manaus.reactor.betting.proposer.common

import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.BetEventTestFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class FairnessProposerTest {

    private val layProposer = FairnessProposer(Side.LAY) { 0.02 }

    private val backProposer = FairnessProposer(Side.BACK) { 0.02 }

    @Test
    fun `lay price by lay proposer`() {
        val event = BetEventTestFactory.newBetEvent(Side.LAY, 2.5, 3.2)
        val proposedPrice = layProposer.getProposedPrice(event)
        assertEquals(2.96, Price.round(proposedPrice))
    }

    @Test
    fun `back price by lay proposer`() {
        val event = BetEventTestFactory.newBetEvent(Side.BACK, 2.5, 3.5)
        val proposedPrice = layProposer.getProposedPrice(event)
        assertEquals(3.041, Price.round(proposedPrice))
    }

    @Test
    fun `back price by back proposer`() {
        val event = BetEventTestFactory.newBetEvent(Side.BACK, 2.8, 3.5)
        val proposedPrice = backProposer.getProposedPrice(event)
        assertEquals(3.041, Price.round(proposedPrice))
    }

    @Test
    fun `lay price by back proposer`() {
        val event = BetEventTestFactory.newBetEvent(Side.LAY, 2.2, 3.7)
        val proposedPrice = backProposer.getProposedPrice(event)
        assertEquals(2.96, Price.round(proposedPrice))
    }

}

