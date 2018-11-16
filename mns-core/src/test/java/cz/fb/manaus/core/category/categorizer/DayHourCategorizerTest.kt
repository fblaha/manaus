package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DayHourCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DayHourCategorizer

    @Test
    fun `hour categories`() {
        val dayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()

        var event = market.event

        event = event.copy(openDate = dayStart)
        assertThat(categorizer.getCategories(market.copy(event = event)).first(), containsString("0_4"))

        event = event.copy(openDate = dayStart.plus(3 * 60 + 59, ChronoUnit.MINUTES))
        assertThat(categorizer.getCategories(market.copy(event = event)).first(), containsString("0_4"))

        event = event.copy(openDate = dayStart.plus(4, ChronoUnit.HOURS))
        assertThat(categorizer.getCategories(market.copy(event = event)).first(), containsString("4_8"))

        event = event.copy(openDate = dayStart.plus(23, ChronoUnit.HOURS))
        assertThat(categorizer.getCategories(market.copy(event = event)).first(), containsString("20_24"))
    }
}
