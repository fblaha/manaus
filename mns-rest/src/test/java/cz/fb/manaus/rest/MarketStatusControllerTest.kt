package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.betTemplate
import org.junit.Test
import java.time.Instant
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MarketStatusControllerTest : AbstractControllerTest() {

    @Test
    fun statuses() {
        val status = MarketStatus(
                id = "777",
                openDate = Instant.now(),
                lastEvent = Instant.now(),
                bets = listOf(betTemplate)
        )
        marketStatusRepository.save(status)
        checkResponse("/statuses", "777")
    }
}