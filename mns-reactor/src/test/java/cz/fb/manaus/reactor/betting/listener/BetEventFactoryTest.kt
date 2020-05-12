package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.price.Fairness
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BetEventFactoryTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var betEventFactory: BetEventFactory

    @Test
    fun create() {
        val snapshot = MarketSnapshot(
                runnerPrices = runnerPrices,
                currentBets = emptyList(),
                market = market
        )

        val fairness = Fairness(0.9, null)
        val sideSelection = SideSelection(Side.BACK, homePrices.selectionId)
        val event = betEventFactory.create(
                sideSelection = sideSelection,
                snapshot = snapshot,
                fairness = fairness,
                account = mbAccount)
        assertEquals(mbAccount, event.account)
        assertEquals(homePrices.selectionId, event.sideSelection.selectionId)
        assertEquals(homePrices, event.runnerPrices)
        assertEquals(Side.BACK, event.side)
    }

}