package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.marketTemplate
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.temporal.ChronoUnit
import java.util.*

class DayHourCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DayHourCategorizer

    @Test
    fun `hout categories`() {
        val dayStart = DateUtils.truncate(Date(), Calendar.MONTH)
        var event = marketTemplate.event

        event = event.copy(openDate = dayStart.toInstant())
        assertThat(categorizer.getCategories(marketTemplate.copy(event = event)).first(), containsString("0_4"))

        event = event.copy(openDate = dayStart.toInstant().plus(3 * 60 + 59, ChronoUnit.MINUTES))
        assertThat(categorizer.getCategories(marketTemplate.copy(event = event)).first(), containsString("0_4"))

        event = event.copy(openDate = dayStart.toInstant().plus(4, ChronoUnit.HOURS))
        assertThat(categorizer.getCategories(marketTemplate.copy(event = event)).first(), containsString("4_8"))

        event = event.copy(openDate = dayStart.toInstant().plus(23, ChronoUnit.HOURS))
        assertThat(categorizer.getCategories(marketTemplate.copy(event = event)).first(), containsString("20_24"))
    }
}
