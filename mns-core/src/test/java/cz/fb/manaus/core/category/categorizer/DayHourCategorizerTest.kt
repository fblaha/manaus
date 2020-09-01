package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.test.assertTrue

class DayHourCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DayHourCategorizer

    @Test
    fun `hour categories`() {
        val dayStart = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant()
        var event = market.event

        event = event.copy(openDate = dayStart)
        assertTrue { "0_3" in categorizer.getCategories(market.copy(event = event)).first() }

        event = event.copy(openDate = dayStart.plus(3 * 60 + 59, ChronoUnit.MINUTES))
        assertTrue { "0_3" in categorizer.getCategories(market.copy(event = event)).first() }

        event = event.copy(openDate = dayStart.plus(4, ChronoUnit.HOURS))
        assertTrue { "4_7" in categorizer.getCategories(market.copy(event = event)).first() }

        event = event.copy(openDate = dayStart.plus(23, ChronoUnit.HOURS))
        assertTrue { "20_23" in categorizer.getCategories(market.copy(event = event)).first() }
    }
}
