package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.Event
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.apache.commons.lang3.time.DateUtils.addDays
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookAheadFilterTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var lookAheadFilter: LookAheadFilter

    @Test
    fun testAccept() {
        val currDate = Date()
        val market = mock(Market::class.java)
        val event = mock(Event::class.java)
        `when`(market.event).thenReturn(event)
        `when`(event.openDate).thenReturn(addDays(currDate, 50), addDays(currDate, 2))
        assertFalse(lookAheadFilter.accept(market, setOf()))
        assertTrue(lookAheadFilter.accept(market, setOf()))
    }
}
