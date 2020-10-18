package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Test
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class MarketRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        marketRepository.save(market)
        val fromDB = marketRepository.read("2") ?: error("must")
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
        assertEquals(0, marketRepository.delete(market.event.openDate.minus(1, ChronoUnit.DAYS)))
        assertNotNull(marketRepository.read("2"))
        assertEquals(1, marketRepository.delete(market.event.openDate.plusSeconds(10)))
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `find markets from`() {
        marketRepository.save(market)
        assertEquals(1, marketRepository.find().size)
        assertEquals(0, marketRepository.find(from = market.event.openDate.plusSeconds(300)).size)
        assertEquals(1, marketRepository.find(from = market.event.openDate.minusSeconds(300)).size)
    }

    @Test
    fun `find markets to`() {
        marketRepository.save(market)
        val minus30 = market.event.openDate.minusSeconds(30)
        val plus30 = market.event.openDate.plusSeconds(30)
        assertEquals(1, marketRepository.find(from = minus30, to = plus30).size)
        assertEquals(0, marketRepository.find(from = minus30, to = minus30).size)
    }

    @Test
    fun `find markets limit`() {
        marketRepository.save(market)
        marketRepository.save(market.copy(id = "3"))
        assertEquals(2, marketRepository.find(maxResults = 2).size)
        assertEquals(1, marketRepository.find(maxResults = 1).size)
    }

    @Test
    fun `find markets sort`() {
        val laterEvent = market.event.copy(openDate = market.event.openDate.plusSeconds(30))
        marketRepository.save(market.copy(id = "3", event = laterEvent))
        marketRepository.save(market)
        val markets = marketRepository.find()
        assertEquals(2, markets.size)
        assertEquals("2", markets.first().id)
        assertEquals("3", markets.last().id)
    }

    @Test
    fun `find markets IDs`() {
        marketRepository.save(market)
        assertEquals(listOf("2"), marketRepository.findIDs())
    }
}
