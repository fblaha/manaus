package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.price.Fairness
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

val HOME_EVENT_BACK: BetEvent = BetEvent(
        sideSelection = SideSelection(Side.BACK, SEL_HOME),
        account = mbAccount,
        coverage = mutableMapOf(),
        marketPrices = runnerPrices,
        market = market,
        metrics = BetMetrics(
                actualTradedVolume = tradedVolume[SEL_HOME],
                fairness = Fairness(0.9, 1.1),
                chargeGrowthForecast = 1.0
        )
)

val HOME_EVENT_LAY: BetEvent = HOME_EVENT_BACK.copy(sideSelection = SideSelection(Side.LAY, SEL_HOME))


class BetEventTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var factory: BetEventTestFactory

    @Test
    fun `old matched`() {
        assertFalse { factory.newBetEvent(Side.BACK, 3.5, 4.6).isOldMatched }
    }

    @Test
    fun `simulate settled bet`() {
        val event = factory.newBetEvent(Side.LAY, 3.5, 4.6).copy(proposedPrice = Price(3.0, 5.0, Side.LAY))
        val settledBet = event.simulatedBet
        assertEquals(event.sideSelection.side, settledBet.settledBet.price.side)
        Assert.assertEquals(5.0, settledBet.settledBet.price.amount, 0.0001)
        assertNotNull(settledBet.betAction)
    }

    @Test
    fun placeOrUpdate() {
        val price = Price(3.0, 3.0, Side.BACK)
        val homeEvent = HOME_EVENT_BACK.copy(proposedPrice = price)
        val command = homeEvent.placeOrUpdate(emptySet())
        assertEquals(price, command.action?.price)
        assertEquals(BetActionType.PLACE, command.action?.betActionType)
        assertEquals(price, command.bet.requestedPrice)
    }

    @Test
    fun cancel() {
        assertFalse { HOME_EVENT_BACK.cancelable }
    }
}

