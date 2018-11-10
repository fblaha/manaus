package cz.fb.manaus.core.repository.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

val market = Market(id = "2",
        name = "Match Odds",
        isInPlay = true,
        type = "match_odds",
        matchedAmount = 100.0,
        event = Event(
                id = "100",
                name = "Sparta vs Ostrava",
                openDate = Instant.now(),
                timezone = "UTC",
                countryCode = "cz",
                venue = "letna"),
        competition = Competition("100", "Czech League"),
        eventType = EventType("1000", "soccer"),
        runners = listOf(Runner(100, "Banik Ostrava", 0.0, 0))
)


class MarketTest {

    @Test
    fun `runner by selection id`() {
        assertEquals(market.runners.first(), market.getRunner(100))
    }
}