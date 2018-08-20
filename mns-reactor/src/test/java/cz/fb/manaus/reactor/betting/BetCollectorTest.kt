package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BetCollectorTest {
    private val marketId = "44"
    private val selectionId: Long = 111

    @Test
    fun `find bet`() {
        val updateBet = Bet("777", marketId, selectionId, Price(5.0, 5.0, Side.LAY), null, 0.0)
        var collector = BetCollector()
        collector.updateBet(BetCommand(updateBet, BetAction()))
        checkCollector(collector)
        collector = BetCollector()
        val placeBet = Bet(null, marketId, selectionId, Price(5.0, 5.0, Side.LAY), null, 0.0)
        collector.placeBet(BetCommand(placeBet, BetAction()))
        checkCollector(collector)
    }

    private fun checkCollector(collector: BetCollector) {
        assertTrue(collector.findBet(marketId, selectionId, Side.LAY).isPresent)
        assertFalse(collector.findBet(marketId, selectionId, Side.BACK).isPresent)
        assertFalse(collector.findBet(marketId + 1, selectionId, Side.LAY).isPresent)
        assertFalse(collector.findBet(marketId, selectionId + 1, Side.LAY).isPresent)
    }

    @Test
    fun `empty collector`() {
        val collector = BetCollector()
        assertTrue(collector.isEmpty)
    }
}
