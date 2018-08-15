package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Event
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.apache.commons.lang3.time.DateUtils.addMinutes
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class DayHourCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DayHourCategorizer

    @Test
    fun testGetCategories() {
        val dayStart = DateUtils.truncate(Date(), Calendar.MONTH)
        val market = mock(Market::class.java)
        val event = mock(Event::class.java)
        `when`(market.event).thenReturn(event)
        `when`(event.openDate).thenReturn(dayStart)
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("0_4"))

        `when`(event.openDate).thenReturn(addMinutes(dayStart, 3 * 60 + 59))
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("0_4"))

        `when`(event.openDate).thenReturn(addHours(dayStart, 4))
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("4_8"))

        `when`(event.openDate).thenReturn(addHours(dayStart, 23))
        assertThat(categorizer.getCategories(market).iterator().next(), containsString("20_24"))
    }
}
