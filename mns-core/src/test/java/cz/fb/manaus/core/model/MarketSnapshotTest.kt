package cz.fb.manaus.core.model

import org.apache.commons.lang3.time.DateUtils
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class MarketSnapshotTest {

    @Test
    fun `market coverage`() {
        val selectionId = 1L
        val side = Side.LAY
        val predecessor = Bet(marketId = "1",
                selectionId = selectionId,
                requestedPrice = Price(2.0, 2.0, side),
                placedDate = DateUtils.addHours(Date(), -2),
                matchedAmount = 1.0)
        val successor = Bet(marketId = "1",
                selectionId = selectionId,
                requestedPrice = Price(2.0, 2.0, side),
                placedDate = Date(),
                matchedAmount = 1.0)
        val coverage = getMarketCoverage(listOf(successor, predecessor))
        assertEquals(1, coverage.size())
        assertEquals(successor, coverage.get(side, selectionId))
    }

}