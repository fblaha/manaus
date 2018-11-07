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
            autoCompact = false
        }
        marketRepository = MarketRepository(db)
    }

    @Test
    fun `save - read`() {
        marketRepository.save(market)
        val fromDB = marketRepository.read("2")
        assertEquals(market, fromDB)
    }

    @Test
    fun `save - delete - read`() {
        marketRepository.save(market)
        assertNotNull(marketRepository.read("2"))
        marketRepository.delete("2")
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `delete older then`() {
        marketRepository.save(market)
        assertEquals(0, marketRepository.deleteMarkets(Instant.now().minusSeconds(10)))
        assertNotNull(marketRepository.read("2"))
        assertEquals(1, marketRepository.deleteMarkets(Instant.now().plusSeconds(10)))
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `get markets from`() {
        marketRepository.save(market)
        assertEquals(1, marketRepository.getMarkets(null, null, null).size)
        assertEquals(0, marketRepository.getMarkets(Instant.now().plusSeconds(30), null, null).size)
        assertEquals(1, marketRepository.getMarkets(Instant.now().minusSeconds(30), null, null).size)
    }

    @Test
    fun `get markets to`() {
        marketRepository.save(market)
        val minus30 = Instant.now().minusSeconds(30)
        val plus30 = Instant.now().plusSeconds(30)
        assertEquals(1, marketRepository.getMarkets(minus30, plus30, null).size)
        assertEquals(0, marketRepository.getMarkets(minus30, minus30, null).size)
    }

    @Test
    fun `get markets limit`() {
        marketRepository.save(market)
        marketRepository.save(market.copy(id = "3"))
        assertEquals(2, marketRepository.getMarkets(null, null, 2).size)
        assertEquals(1, marketRepository.getMarkets(null, null, 1).size)
    }

    @Test
    fun `get markets sort`() {
        val laterEvent = market.event.copy(openDate = Instant.now().plusSeconds(30))
        marketRepository.save(market.copy(id = "3", event = laterEvent))
        marketRepository.save(market)
        val markets = marketRepository.getMarkets(null, null, null)
        assertEquals(2, markets.size)
        assertEquals("2", markets.first().id)
        assertEquals("3", markets.last().id)
    }
}
