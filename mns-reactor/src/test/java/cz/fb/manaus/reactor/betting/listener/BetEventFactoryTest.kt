package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.SideSelection
import cz.fb.manaus.core.model.account
import cz.fb.manaus.core.model.homePrices
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
        val fairness = Fairness(0.9, null)
        val sideSelection = SideSelection(Side.BACK, homePrices.selectionId)
        val event = betEventFactory.create(
                sideSelection = sideSelection,
                snapshot = snapshot,
                fairness = fairness,
                account = account)
        assertEquals(account, event.account)
        assertEquals(homePrices.selectionId, event.selectionId)
        assertEquals(homePrices, event.runnerPrices)
        assertEquals(Side.BACK, event.side)
    }

}