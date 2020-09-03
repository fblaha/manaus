package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class MarketRepositoryTest : AbstractIntegrationTestCase() {

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
        assertEquals(0, marketRepository.delete(market.openDate.minusSeconds(10)))
        assertNotNull(marketRepository.read("2"))
        assertEquals(1, marketRepository.delete(market.openDate.plusSeconds(10)))
        assertNull(marketRepository.read("2"))
    }

    @Test
    fun `find markets from`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(1, marketRepository.find().size)
        assertEquals(0, marketRepository.find(from = market.openDate.plusSeconds(30)).size)
        assertEquals(1, marketRepository.find(from = market.openDate.minusSeconds(30)).size)
    }

    @Test
    fun `find markets to`() {
        marketRepository.saveOrUpdate(market)
        val minus30 = market.openDate.minusSeconds(30)
        val plus30 = market.openDate.plusSeconds(30)
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
        val laterEvent = market.event.copy(openDate = market.openDate.plusSeconds(30))
        marketRepository.saveOrUpdate(market.copy(id = "3", event = laterEvent))
        marketRepository.saveOrUpdate(market)
        val markets = marketRepository.find()
        assertEquals(2, markets.size)
        assertEquals("2", markets.first().id)
        assertEquals("3", markets.last().id)
    }

    @Test
    fun `find markets IDs`() {
        marketRepository.saveOrUpdate(market)
        assertEquals(listOf("2"), marketRepository.findIDs())
    }
}
