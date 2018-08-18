package cz.fb.manaus.core.manager.filter

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.Event
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.apache.commons.lang3.time.DateUtils.addDays
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookAheadFilterTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var lookAheadFilter: LookAheadFilter

    @Test
    fun `look ahead filtering`() {
        val currDate = Date()
        val market = mock<Market>()
        val event = mock<Event>()
        whenever(market.event).thenReturn(event)
        whenever(event.openDate).thenReturn(addDays(currDate, 50), addDays(currDate, 2))
        assertFalse(lookAheadFilter.accept(market, setOf()))
        assertTrue(lookAheadFilter.accept(market, setOf()))
    }
}
