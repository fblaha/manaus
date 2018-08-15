package cz.fb.manaus.core.dao

import cz.fb.manaus.core.model.EventTest
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.newMarket
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.DateUtils.addHours
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Date.from
import java.util.Optional.of

class MarketDaoTest : AbstractDaoTest() {

    @Test
    fun testMarketGet() {
        val curr = Date()
        marketDao.saveOrUpdate(newMarket(CoreTestFactory.MARKET_ID, curr, CoreTestFactory.MATCH_ODDS))
        assertThat(marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(of(addHours(curr, -1)), Optional.empty(), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(of(addHours(curr, -1)), of(addHours(curr, 1)), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(of(curr), of(curr), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(of(addHours(curr, 1)), Optional.empty(), OptionalInt.empty()).size, `is`(0))

        assertThat(marketDao.getMarkets(Optional.empty(), of(addHours(curr, 1)), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(Optional.empty(), of(curr), OptionalInt.empty()).size, `is`(1))
        assertThat(marketDao.getMarkets(Optional.empty(), of(addHours(curr, -1)), OptionalInt.empty()).size, `is`(0))
    }

    @Test
    fun testMarketOrder() {
        val date = DateUtils.truncate(Date(), Calendar.MONTH)
        marketDao.saveOrUpdate(newMarket("33", addHours(date, 2), CoreTestFactory.MATCH_ODDS))
        marketDao.saveOrUpdate(newMarket("22", addHours(date, 2), CoreTestFactory.MATCH_ODDS))
        marketDao.saveOrUpdate(newMarket("44", addHours(date, 1), CoreTestFactory.MATCH_ODDS))
        val markets = marketDao.getMarkets(Optional.empty(), Optional.empty(), OptionalInt.empty())
        assertThat(markets[0].id, `is`("44"))
        assertThat(markets[1].id, `is`("22"))
        assertThat(markets[2].id, `is`("33"))
    }

    @Test
    fun testMarketSave() {
        marketDao.saveOrUpdate(newMarket())
    }

    @Test
    fun testRunner() {
        marketDao.saveOrUpdate(newMarket())
        val market = marketDao.get(CoreTestFactory.MARKET_ID).get()
        assertThat(market.runners.size, `is`(3))

        assertThat(market.runners.stream().findFirst().get().name,
                `is`(CoreTestFactory.HOME_NAME))
        assertThat(market.runners.stream().skip(1).findFirst().get().name,
                `is`(CoreTestFactory.DRAW_NAME))
        assertThat(market.runners.stream().skip(2).findFirst().get().name,
                `is`(CoreTestFactory.AWAY_NAME))
    }

    @Test
    fun testMarketMerge() {
        var market = newMarket()
        val childA = EventTest.create("55", "childA", Date(), CoreTestFactory.COUNTRY_CODE)
        market.event = childA
        marketDao.saveOrUpdate(market)
        val toBeMerged = newMarket(CoreTestFactory.MARKET_ID, Date(), AbstractDaoTest.SPARTA)
        val childB = EventTest.create("55", "childB", Date(), CoreTestFactory.COUNTRY_CODE)
        toBeMerged.event = childB
        marketDao.saveOrUpdate(toBeMerged)
        market = marketDao.get(CoreTestFactory.MARKET_ID).get()
        assertThat(market.event.id, `is`("55"))
        assertThat(market.event.name, `is`("childB"))
        assertThat(market.name, `is`(AbstractDaoTest.SPARTA))
        assertThat(market.event.countryCode, `is`(CoreTestFactory.COUNTRY_CODE))
    }

    @Test
    fun testMarketSaveSubsequentUpdate() {
        var market = newMarket()
        marketDao.saveOrUpdate(market)
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().runners.size, `is`(3))
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), "new name")
        marketDao.saveOrUpdate(market)
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().name, `is`("new name"))
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().event.countryCode, `is`(CoreTestFactory.COUNTRY_CODE))
    }

    @Test
    fun testMarketVersion() {
        var market = newMarket()
        marketDao.saveOrUpdate(market)
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().version, `is`(0))
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), AbstractDaoTest.SPARTA)
        marketDao.saveOrUpdate(market)
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().version, `is`(1))
        market = newMarket(CoreTestFactory.MARKET_ID, Date(), "yet another")
        marketDao.saveOrUpdate(market)
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).get().version, `is`(2))
    }

    @Test
    fun testMarketBulkDelete() {
        createBet()
        var count = marketDao.deleteMarkets(from(Instant.now().minus(1, ChronoUnit.HOURS)))
        assertThat(count, `is`(0))
        count = marketDao.deleteMarkets(from(Instant.now().plus(3, ChronoUnit.HOURS)))
        assertThat(count, `is`(1))
        assertThat(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size, `is`(0))
        assertThat(marketDao.get(CoreTestFactory.MARKET_ID).orElse(null), nullValue())
    }

}