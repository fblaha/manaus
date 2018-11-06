package cz.fb.manaus.repository

import cz.fb.manaus.core.persistence.*
import org.dizitart.kno2.nitrite
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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


class MarketRepositoryTest {

    private lateinit var marketRepository: MarketRepository

    @Before
    fun setUp() {
        val db = nitrite {
            autoCommitBufferSize = 2048
            compress = true
            autoCompact = false
        }
        marketRepository = MarketRepository(db)
    }

    @Test
    fun `save and read`() {
        marketRepository.saveOrUpdate(market)
        val fromDB = marketRepository.read("2")
        assertEquals(market, fromDB)
    }

    @Test
    fun `delete older then`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(0, marketRepository.deleteMarkets(Instant.now().minusSeconds(10)))
        assertNotNull(marketRepository.read("2"))
        assertEquals(1, marketRepository.deleteMarkets(Instant.now().plusSeconds(10)))
        assertNull(marketRepository.read("2"))
    }
}
