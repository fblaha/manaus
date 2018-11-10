package cz.fb.manaus.core.repository

import cz.fb.manaus.core.repository.domain.*
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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


class MarketRepositoryTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var marketRepository: MarketRepository


    @Before
    fun setUp() {
        marketRepository.repository.remove(ObjectFilters.ALL)
    }

    @Test
    fun `save - read`() {
        marketRepository.saveOrUpdate(market)
        val fromDB = marketRepository.read("2")
        assertEquals(market, fromDB)
    }

    @Test
    fun `save - delete - read`() {
        marketRepository.saveOrUpdate(market)
        assertNotNull(marketRepository.read("2"))
        marketRepository.delete("2")
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `delete older then`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(0, marketRepository.delete(Instant.now().minusSeconds(10)))
        assertNotNull(marketRepository.read("2"))
        assertEquals(1, marketRepository.delete(Instant.now().plusSeconds(10)))
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `find markets from`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(1, marketRepository.find().size)
        assertEquals(0, marketRepository.find(from = Instant.now().plusSeconds(30)).size)
        assertEquals(1, marketRepository.find(from = Instant.now().minusSeconds(30)).size)
    }

    @Test
    fun `find markets to`() {
        marketRepository.saveOrUpdate(market)
        val minus30 = Instant.now().minusSeconds(30)
        val plus30 = Instant.now().plusSeconds(30)
        assertEquals(1, marketRepository.find(from = minus30, to = plus30).size)
        assertEquals(0, marketRepository.find(from = minus30, to = minus30).size)
    }

    @Test
    fun `find markets limit`() {
        marketRepository.saveOrUpdate(market)
        marketRepository.saveOrUpdate(market.copy(id = "3"))
        assertEquals(2, marketRepository.find(maxResults = 2).size)
        assertEquals(1, marketRepository.find(maxResults = 1).size)
    }

    @Test
    fun `find markets sort`() {
        val laterEvent = market.event.copy(openDate = Instant.now().plusSeconds(30))
        marketRepository.saveOrUpdate(market.copy(id = "3", event = laterEvent))
        marketRepository.saveOrUpdate(market)
        val markets = marketRepository.find()
        assertEquals(2, markets.size)
        assertEquals("2", markets.first().id)
        assertEquals("3", markets.last().id)
    }
}
