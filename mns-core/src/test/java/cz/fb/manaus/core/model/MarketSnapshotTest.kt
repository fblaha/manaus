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
        val predecessor = Bet(null, "1", selectionId, Price(2.0, 2.0, side), DateUtils.addHours(Date(), -2), 1.0)
        val successor = Bet(null, "1", selectionId, Price(2.0, 2.0, side), Date(), 1.0)
        val coverage = MarketSnapshot.getMarketCoverage(listOf(successor, predecessor))
        assertEquals(1, coverage.size())
        assertEquals(successor, coverage.get(side, selectionId))
    }

}