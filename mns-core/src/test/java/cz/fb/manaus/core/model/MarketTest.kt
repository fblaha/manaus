package cz.fb.manaus.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MarketTest {

    @Test
    fun `runner by selection id`() {
        assertEquals(marketTemplate.runners.first(), marketTemplate.getRunner(100))
    }
}