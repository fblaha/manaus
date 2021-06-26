package cz.fb.manaus.rest

import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.model.betTemplate
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.model.status
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MarketStatusControllerTest : AbstractControllerTest() {

    @Test
    fun statuses() {
        val status = MarketStatus(
                id = "777",
                eventDate = Instant.now(),
                eventName = market.event.name,
                lastEvent = Instant.now(),
                bets = listOf(betTemplate.status)
        )
        marketStatusRepository.save(status)
        checkResponse("/statuses", "777")
    }
}