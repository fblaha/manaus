package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookAheadFilterTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var lookAheadFilter: LookAheadFilter

    @Test
    fun `look ahead filtering`() {
        val event = market.event
        val plus50d = Instant.now().plus(50, ChronoUnit.DAYS)
        val plus5d = Instant.now().plus(5, ChronoUnit.DAYS)
        assertFalse(lookAheadFilter.accept(market.copy(event = event.copy(openDate = plus50d)), setOf()))
        assertTrue(lookAheadFilter.accept(market.copy(event = event.copy(openDate = plus5d)), setOf()))
    }
}
