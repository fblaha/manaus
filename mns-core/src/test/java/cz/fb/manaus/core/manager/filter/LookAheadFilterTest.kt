package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.repository.domain.marketTemplate
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
        val event = marketTemplate.event
        val plus50d = Instant.now().plus(50, ChronoUnit.DAYS)
        val plus5d = Instant.now().plus(5, ChronoUnit.DAYS)
        assertFalse(lookAheadFilter.accept(marketTemplate.copy(event = event.copy(openDate = plus50d)), setOf()))
        assertTrue(lookAheadFilter.accept(marketTemplate.copy(event = event.copy(openDate = plus5d)), setOf()))
    }
}
