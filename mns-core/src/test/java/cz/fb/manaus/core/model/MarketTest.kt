package cz.fb.manaus.core.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MarketTest {

    @Test
    fun `runner by selection id`() {
        assertEquals(market.runners.first(), market.getRunner(100))
    }
}