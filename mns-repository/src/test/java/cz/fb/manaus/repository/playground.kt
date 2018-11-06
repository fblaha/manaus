package cz.fb.manaus.core.persistence

import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.junit.Test
import java.util.*


class NitritePlaygroundTest {

    @Test
    fun `insert market`() {
        val event = Event(
                id = "100",
                name = "Sparta vs Ostrava",
                openDate = Date(),
                timezone = "UTC",
                countryCode = "cz",
                venue = "letna")

        val market = Market(id = "2",
                name = "Match Odds",
                isInPlay = true,
                type = "match_odds",
                matchedAmount = 100.0,
                event = event,
                competition = Competition("100", "Czech League"),
                eventType = EventType("1000", "soccer"),
                runners = listOf(Runner(100, "Banik Ostrava", 0.0, 0))
        )

        val db = nitrite {
            autoCommitBufferSize = 2048
            compress = true
            autoCompact = false
        }

        val repository = db.getRepository<Market> {}
        repository.insert(market)
        for (m in repository.find(Market::id eq "2")) {
            println(m)
        }
    }
}
