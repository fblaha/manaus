package cz.fb.manaus.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MarketTest {

    @Test
    fun `runner by selection id`() {
        assertEquals(market.runners.first(), market.getRunner(100))
    }
}