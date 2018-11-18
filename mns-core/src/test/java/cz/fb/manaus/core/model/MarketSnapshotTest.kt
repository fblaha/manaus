package cz.fb.manaus.core.model

import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class MarketSnapshotTest {

    @Test
    fun `market coverage`() {
        val selectionId = 1L
        val side = Side.LAY
        val predecessor = Bet(marketId = "1",
                selectionId = selectionId,
                requestedPrice = Price(2.0, 2.0, side),
                placedDate = Instant.now().minus(2, ChronoUnit.HOURS),
                matchedAmount = 1.0)
        val successor = Bet(marketId = "1",
                selectionId = selectionId,
                requestedPrice = Price(2.0, 2.0, side),
                placedDate = Instant.now(),
                matchedAmount = 1.0)
        val coverage = getMarketCoverage(listOf(successor, predecessor))
        assertEquals(1, coverage.size)
        assertEquals(successor, coverage[SideSelection(side, selectionId)])
    }

}