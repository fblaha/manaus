package cz.fb.manaus.core.dao

import cz.fb.manaus.core.model.ModelFactory
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.*
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Date.from
import java.util.Optional.of
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MarketDaoTest : AbstractDaoTest() {

    @Test
    fun `get criteria`() {
        val curr = Date()
        marketDao.saveOrUpdate(newMarket(CoreTestFactory.MARKET_ID, curr, CoreTestFactory.MATCH_ODDS))
        assertEquals(1, marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty()).size)
        assertEquals(1, marketDao.getMarkets(of(addHours(curr, -1)), Optional.empty(), OptionalInt.empty()).size)
        assertEquals(1, marketDao.getMarkets(of(addHours(curr, -1)), of(addHours(curr, 1)), OptionalInt.empty()).size)
        assertEquals(1, marketDao.getMarkets(of(curr), of(curr), OptionalInt.empty()).size)
        assertEquals(0, marketDao.getMarkets(of(addHours(curr, 1)), Optional.empty(), OptionalInt.empty()).size)

        assertEquals(1, marketDao.getMarkets(Optional.empty(), of(addHours(curr, 1)), OptionalInt.empty()).size)
        assertEquals(1, marketDao.getMarkets(Optional.empty(), of(curr), OptionalInt.empty()).size)
        assertEquals(0, marketDao.getMarkets(Optional.empty(), of(addHours(curr, -1)), OptionalInt.empty()).size)
    }

    @Test
    fun `get market order`() {
        val date = DateUtils.truncate(Date(), Calendar.MONTH)
        marketDao.saveOrUpdate(newMarket("33", addHours(date, 2), CoreTestFactory.MATCH_ODDS))
        marketDao.saveOrUpdate(newMarket("22", addHours(date, 2), CoreTestFactory.MATCH_ODDS))
        marketDao.saveOrUpdate(newMarket("44", addHours(date, 1), CoreTestFactory.MATCH_ODDS))
        val markets = marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty())
        assertEquals("44", markets[0].id)
        assertEquals("22", markets[1].id)
        assertEquals("33", markets[2].id)
    }

    @Test
    fun `market save`() {
        marketDao.saveOrUpdate(newMarket())
    }

    @Test
    fun `runner saved`() {
        marketDao.saveOrUpdate(newMarket())
        val market = marketDao.get(CoreTestFactory.MARKET_ID).get()
        assertEquals(3, market.runners.size)

        assertEquals(HOME_NAME, market.runners.stream().findFirst().get().name)
        assertEquals(DRAW_NAME, market.runners.stream().skip(1).findFirst().get().name)
        assertEquals(AWAY_NAME, market.runners.stream().skip(2).findFirst().get().name)
    }

    @Test
    fun `market merge`() {
        var market = newMarket()
        val childA = ModelFactory.newEvent("55", "childA", Date(), COUNTRY_CODE)
        market.event = childA
        marketDao.saveOrUpdate(market)
        val toBeMerged = newMarket(CoreTestFactory.MARKET_ID, Date(), AbstractDaoTest.SPARTA)
        val childB = ModelFactory.newEvent("55", "childB", Date(), COUNTRY_CODE)
        toBeMerged.event = childB
        marketDao.saveOrUpdate(toBeMerged)
        market = marketDao.get(CoreTestFactory.MARKET_ID).get()
        assertEquals("55", market.event.id)
        assertEquals("childB", market.event.name)
        assertEquals(AbstractDaoTest.SPARTA, market.name)
        assertEquals(CoreTestFactory.COUNTRY_CODE, market.event.countryCode)
    }

    @Test
    fun `market save and then update`() {
        var market = newMarket()
        marketDao.saveOrUpdate(market)
        assertEquals(3, marketDao.get(CoreTestFactory.MARKET_ID).get().runners.size)
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), "new name")
        marketDao.saveOrUpdate(market)
        assertEquals("new name", marketDao.get(CoreTestFactory.MARKET_ID).get().name)
        assertEquals(CoreTestFactory.COUNTRY_CODE, marketDao.get(CoreTestFactory.MARKET_ID).get().event.countryCode)
    }

    @Test
    fun `market version`() {
        var market = newMarket()
        marketDao.saveOrUpdate(market)
        assertEquals(0, marketDao.get(CoreTestFactory.MARKET_ID).get().version)
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), AbstractDaoTest.SPARTA)
        marketDao.saveOrUpdate(market)
        assertEquals(1, marketDao.get(CoreTestFactory.MARKET_ID).get().version)
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), "yet another")
        marketDao.saveOrUpdate(market)
        assertEquals(2, marketDao.get(CoreTestFactory.MARKET_ID).get().version)
    }

    @Test
    fun `market bulk delete`() {
        createBet()
        var count = marketDao.deleteMarkets(from(Instant.now().minus(1, ChronoUnit.HOURS)))
        assertEquals(0, count)
        count = marketDao.deleteMarkets(from(Instant.now().plus(3, ChronoUnit.HOURS)))
        assertEquals(1, count)
        assertEquals(0, marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size)
        assertNull(marketDao.get(CoreTestFactory.MARKET_ID).orElse(null))
    }
}