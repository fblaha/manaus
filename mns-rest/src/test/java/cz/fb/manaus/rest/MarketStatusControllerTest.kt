package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketStatus
import org.junit.Test
import java.time.Instant
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MarketStatusControllerTest : AbstractControllerTest() {

    @Test
    fun statuses() {
        val entity = MarketStatus("777", Instant.now(), Instant.now())
        marketStatusRepository.save(entity)
        checkResponse("/statuses", "777")
    }
}