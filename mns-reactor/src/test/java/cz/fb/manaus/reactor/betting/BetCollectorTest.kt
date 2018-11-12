package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.betAction
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BetCollectorTest {
    private val marketId = "44"
    private val selectionId: Long = 111

    @Test
    fun `find bet`() {
        val updateBet = Bet(betId = "777", marketId = marketId, selectionId = selectionId,
                requestedPrice = Price(5.0, 5.0, Side.LAY))
        var collector = BetCollector()
        collector.updateBet(BetCommand(updateBet, betAction))
        checkCollector(collector)
        collector = BetCollector()
        val placeBet = Bet(betId = null, marketId = marketId, selectionId = selectionId,
                requestedPrice = Price(5.0, 5.0, Side.LAY))
        collector.placeBet(BetCommand(placeBet, betAction))
        checkCollector(collector)
    }

    private fun checkCollector(collector: BetCollector) {
        assertNotNull(collector.findBet(marketId, selectionId, Side.LAY))
        assertNull(collector.findBet(marketId, selectionId, Side.BACK))
        assertNull(collector.findBet(marketId + 1, selectionId, Side.LAY))
        assertNull(collector.findBet(marketId, selectionId + 1, Side.LAY))
    }

    @Test
    fun `empty collector`() {
        val collector = BetCollector()
        assertTrue(collector.isEmpty)
    }
}
