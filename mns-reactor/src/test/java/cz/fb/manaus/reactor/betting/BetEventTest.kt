package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.BetEventTestFactory
import cz.fb.manaus.reactor.price.Fairness
import org.junit.jupiter.api.Test
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


class BetEventTest {

    @Test
    fun `old matched`() {
        assertFalse { BetEventTestFactory.newBetEvent(Side.BACK, 3.5, 4.6).isOldMatched }
    }

    @Test
    fun `simulate settled bet`() {
        val event = BetEventTestFactory.newBetEvent(Side.LAY, 3.5, 4.6)
            .copy(proposedPrice = Price(3.0, 5.0, Side.LAY))
        val realized = event.simulatedBet
        val settledBet = realized.settledBet
        assertEquals(event.sideSelection.side, settledBet.price.side)
        assertEquals(5.0, settledBet.price.amount, 0.0001)
        assertEquals(5.0, settledBet.price.amount, 0.0001)
        assertNotNull(realized.betAction)
    }

    @Test
    fun placeOrUpdate() {
        val price = Price(3.0, 3.0, Side.BACK)
        val homeEvent = HOME_EVENT_BACK.copy(proposedPrice = price)
        val command = homeEvent.placeOrUpdate(emptySet())
        assertEquals(price, command.action.price)
        assertEquals(BetActionType.PLACE, command.action.betActionType)
        assertEquals(price, command.bet.remote.requestedPrice)
    }

    @Test
    fun cancel() {
        assertFalse { HOME_EVENT_BACK.cancelable }
    }

    @Test
    fun create() {
        val snapshot = MarketSnapshot(
            runnerPrices = runnerPrices,
            currentBets = emptyList(),
            market = market
        )

        val fairness = Fairness(0.9, null)
        val sideSelection = SideSelection(Side.BACK, homePrices.selectionId)
        val event = createBetEvent(
            sideSelection = sideSelection,
            snapshot = snapshot,
            fairness = fairness,
            account = mbAccount
        )
        assertEquals(mbAccount, event.account)
        assertEquals(homePrices.selectionId, event.sideSelection.selectionId)
        assertEquals(homePrices, event.runnerPrices)
        assertEquals(Side.BACK, event.side)
    }

}

