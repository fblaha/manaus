package cz.fb.manaus.core.dao

import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.model.MarketPrices
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newMarket
import cz.fb.manaus.core.test.CoreTestFactory.Companion.newTestMarket
import cz.fb.manaus.core.test.ModelFactory
import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.math3.util.Precision
import org.hibernate.LazyInitializationException
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarketPricesDaoTest : AbstractDaoTest() {

    @Test
    fun `save and get`() {
        val market = newTestMarket()
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        assertEquals(1, marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size)
    }

    @Test(expected = LazyInitializationException::class)
    fun `market fetched lazily`() {
        val market = newTestMarket()
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        marketPricesDao.getPrices(CoreTestFactory.MARKET_ID)[0].market.name
    }

    @Test
    fun `save and get - 2 prices for the same market`() {
        val market = newTestMarket()
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPrices2 = ModelFactory.newPrices(1, market, listOf(), Date())
        marketPricesDao.saveOrUpdate(marketPrices2)
        assertEquals(2, marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size)
    }

    @Test
    fun `price list order`() {
        createBet()
        val shortList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID, OptionalInt.of(1))
        val marketPricesList = marketPricesDao.getPrices(CoreTestFactory.MARKET_ID)
        assertEquals(2, marketPricesList.size)
        val longFirst = marketPricesList[0]
        assertTrue(longFirst.time.after(marketPricesList[1].time))
        val shortFirst = shortList[0]
        assertEquals(longFirst.time, shortFirst.time)
        assertEquals(longFirst.runnerPrices.first(), shortFirst.runnerPrices.first())
    }

    @Test
    fun `runner prices delete`() {
        val market = newTestMarket()
        val marketPrices = ModelFactory.newPrices(1, market, listOf(
                ModelFactory.newRunnerPrices(232, listOf(Price(2.3, 22.0, Side.BACK)), 5.0, 2.5)), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        marketDao.delete(market.id)
    }

    @Test
    fun `runner prices sort`() {
        val market = newTestMarket()
        val better = Price(2.3, 22.0, Side.BACK)
        val worse = Price(2.2, CoreTestFactory.DRAW.toDouble(), Side.BACK)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(
                ModelFactory.newRunnerPrices(232, listOf(better, worse), 5.0, 2.5)), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPricesList = marketPricesDao.getPrices(market.id)
        assertEquals(better, marketPricesList[0].getRunnerPrices(232).pricesSorted.first())
        assertEquals(worse, marketPricesList[0].getRunnerPrices(232).pricesSorted.last())
    }

    @Test
    fun `runner prices sort - part 2`() {
        val market = newTestMarket()
        val better = Price(2.2, 22.0, Side.BACK)
        val worse = Price(2.2, 22.0, Side.LAY)
        val selId = 232
        val marketPrices = ModelFactory.newPrices(1, market, listOf(
                ModelFactory.newRunnerPrices(selId.toLong(), listOf(better, worse), 5.0, 2.5)), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPricesList = marketPricesDao.getPrices(market.id)
        assertEquals(better, marketPricesList[0].getRunnerPrices(selId.toLong()).pricesSorted.first())
        assertEquals(worse, marketPricesList[0].getRunnerPrices(selId.toLong()).pricesSorted.last())
    }

    @Test
    fun `runner prices sort - part 3`() {
        val market = newTestMarket()
        val better = Price(2.3, 22.0, Side.LAY)
        val worse = Price(2.4, CoreTestFactory.DRAW.toDouble(), Side.LAY)
        val selId = 232
        val marketPrices = ModelFactory.newPrices(1, market, listOf(
                ModelFactory.newRunnerPrices(selId.toLong(), listOf(better, worse), 5.0, 2.5)), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPricesList = marketPricesDao.getPrices(market.id)
        assertEquals(better, marketPricesList[0].getRunnerPrices(selId.toLong()).pricesSorted.first())
        assertEquals(worse, marketPricesList[0].getRunnerPrices(selId.toLong()).pricesSorted.last())
    }

    @Test
    fun `runner prices sort - part 4`() {
        val market = newTestMarket()
        val layBetter = Price(2.3, 22.0, Side.LAY)
        val layWorse = Price(2.4, CoreTestFactory.DRAW.toDouble(), Side.LAY)
        val backBetter = Price(2.3, 22.0, Side.BACK)
        val backWorse = Price(2.2, CoreTestFactory.DRAW.toDouble(), Side.BACK)
        val selId = 232
        val prices = ModelFactory.newPrices(1, market, listOf(
                ModelFactory.newRunnerPrices(selId.toLong(), listOf(layWorse, backWorse, backBetter, layBetter), 5.0, 2.5)), Date())
        prices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(prices)
        val marketPricesList = marketPricesDao.getPrices(market.id)
        val marketPrices = marketPricesList[0]
        assertEquals(4, marketPrices.getRunnerPrices(selId.toLong()).pricesSorted.size)
        assertEquals(backBetter,
                marketPrices.getRunnerPrices(selId.toLong()).pricesSorted.first())
        assertEquals(layWorse, marketPrices.getRunnerPrices(selId.toLong()).pricesSorted.last())
        run {
            val backPrices = marketPrices.getHomogeneous(Side.BACK)
            assertEquals(2, backPrices.getRunnerPrices(selId.toLong()).pricesSorted.size)
            assertEquals(backBetter,
                    backPrices.getRunnerPrices(selId.toLong()).pricesSorted.first())
            assertEquals(backWorse, backPrices.getRunnerPrices(selId.toLong()).pricesSorted.last())
        }
        run {
            val layPrices = marketPrices.getHomogeneous(Side.LAY)
            assertEquals(2, layPrices.getRunnerPrices(selId.toLong()).pricesSorted.size)
            assertEquals(layBetter, layPrices.getRunnerPrices(selId.toLong()).pricesSorted.first())
            assertEquals(layWorse, layPrices.getRunnerPrices(selId.toLong()).pricesSorted.last())
        }
    }

    @Test
    fun `price list order - part 2`() {
        val market = newTestMarket()
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        marketPrices.time = DateUtils.addMonths(Date(), -1)
        marketDao.saveOrUpdate(market)
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPrices2 = ModelFactory.newPrices(1, market, listOf(), Date())
        marketPrices2.time = DateUtils.addMonths(Date(), -2)
        marketPricesDao.saveOrUpdate(marketPrices2)
        assertEquals(2, marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size)
        assertTrue(marketPricesDao.getPrices(CoreTestFactory.MARKET_ID)[0].time.time > marketPricesDao.getPrices(CoreTestFactory.MARKET_ID)[1].time.time)
    }

    @Test
    fun `market delete`() {
        val market = newMarket("55", Date(), CoreTestFactory.MATCH_ODDS)
        marketDao.saveOrUpdate(market)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        marketPricesDao.saveOrUpdate(marketPrices)
        val marketPrices2 = ModelFactory.newPrices(1, market, listOf(), Date())
        marketPricesDao.saveOrUpdate(marketPrices2)
        assertEquals(2, marketPricesDao.getPrices("55").size)
        marketDao.delete("55")
        assertEquals(0, marketPricesDao.getPrices("55").size)
    }

    @Test
    fun `market delete - part 2`() {
        createBet()
        marketDao.delete(CoreTestFactory.MARKET_ID)
        assertEquals(0, marketPricesDao.getPrices(CoreTestFactory.MARKET_ID).size)
    }

    @Test
    fun `runner prices by market and selection`() {
        createBet()
        listOf(CoreTestFactory.HOME, CoreTestFactory.DRAW, CoreTestFactory.AWAY).forEach { selectionId ->
            val shortList = marketPricesDao.getRunnerPrices(CoreTestFactory.MARKET_ID, selectionId, OptionalInt.of(1))
            val complete = marketPricesDao.getRunnerPrices(CoreTestFactory.MARKET_ID, selectionId, OptionalInt.empty())
            assertEquals(2, complete.size)
            assertEquals(selectionId, complete[0].selectionId)
            assertEquals(2.3, complete[0].bestPrice.get().price)
            assertEquals(selectionId, complete[1].selectionId)
            assertEquals(2.1, complete[1].bestPrice.get().price)
            assertEquals(1, shortList.size)
            assertEquals(selectionId, shortList[0].selectionId)
            assertEquals(2.3, shortList[0].bestPrice.get().price)
        }
    }

    @Test
    fun `reciprocal back`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        val marketPrices = newPrices(market, 3.0)
        marketPricesDao.saveOrUpdate(marketPrices)
        val latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(1.0, getMarketReciprocal(latest, Side.BACK).asDouble)
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)
    }

    private fun newPrices(market: Market, bestPrice: Double): MarketPrices {
        val home = ModelFactory.newRunnerPrices(22, listOf(
                Price(2.0, 100.0, Side.BACK),
                Price(bestPrice, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val draw = ModelFactory.newRunnerPrices(22, listOf(
                Price(2.0, 100.0, Side.BACK),
                Price(bestPrice, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val away = ModelFactory.newRunnerPrices(22, listOf(
                Price(bestPrice, 100.0, Side.BACK),
                Price(2.0, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val result = ModelFactory.newPrices(1, market, listOf(home, draw, away), Date())
        result.time = Date()
        return result
    }

    @Test
    fun `reciprocal time sort`() {
        val currDate = Date()
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        savePrices(currDate, market, 3.0)
        savePrices(DateUtils.addDays(currDate, -1), market, 2.7)
        val latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(1.0, getMarketReciprocal(latest, Side.BACK).asDouble)
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)
    }

    @Test
    fun `reciprocal time sort - part 2`() {
        val currDate = Date()
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)

        savePrices(DateUtils.addDays(currDate, -1), market, 2.7)
        var latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(0.9, Precision.round(getMarketReciprocal(latest, Side.BACK).asDouble, 6))
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)

        savePrices(currDate, market, 3.0)
        latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(1.0, getMarketReciprocal(latest, Side.BACK).asDouble)
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)
    }

    private fun savePrices(currDate: Date, market: Market, bestPrice: Double) {
        val marketPrices = newPrices(market, bestPrice)
        marketPrices.time = currDate
        marketPricesDao.saveOrUpdate(marketPrices)
    }

    @Test
    fun `reciprocal back - part 2`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        val home = ModelFactory.newRunnerPrices(22, listOf(
                Price(2.0, 100.0, Side.BACK),
                Price(4.0, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val draw = ModelFactory.newRunnerPrices(33, listOf(
                Price(2.0, 100.0, Side.BACK),
                Price(4.0, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val away = ModelFactory.newRunnerPrices(44, listOf(
                Price(2.0, 100.0, Side.BACK),
                Price(2.0, 100.0, Side.BACK),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(home, draw, away), Date())
        marketPricesDao.saveOrUpdate(marketPrices)
        val latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(1.0, getMarketReciprocal(latest, Side.BACK).asDouble)
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)
    }


    @Test
    fun `reciprocal lay`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        val home = ModelFactory.newRunnerPrices(22, listOf(
                Price(4.0, 100.0, Side.LAY),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val draw = ModelFactory.newRunnerPrices(22, listOf(
                Price(4.0, 100.0, Side.LAY),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val away = ModelFactory.newRunnerPrices(22, listOf(
                Price(2.0, 100.0, Side.LAY),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(home, draw, away), Date())
        marketPricesDao.saveOrUpdate(marketPrices)
        val latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertEquals(1.0, getMarketReciprocal(latest, Side.LAY).asDouble)
        assertEquals(0.5, getMarketReciprocal(latest, Side.BACK).asDouble)
    }

    @Test
    fun `reciprocal absent`() {
        val market = newTestMarket()
        marketDao.saveOrUpdate(market)
        val home = ModelFactory.newRunnerPrices(22, listOf(
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val draw = ModelFactory.newRunnerPrices(22, listOf(
                Price(4.0, 100.0, Side.LAY),
                Price(1.5, 100.0, Side.BACK)), 2.0, 2.0)
        val away = ModelFactory.newRunnerPrices(22, listOf(
                Price(1.5, 100.0, Side.LAY)), 2.0, 2.0)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(home, draw, away), Date())
        marketPricesDao.saveOrUpdate(marketPrices)
        val latest = marketPricesDao.getPrices(market.id, OptionalInt.of(1))
        assertFalse(getMarketReciprocal(latest, Side.LAY).isPresent)
        assertFalse(getMarketReciprocal(latest, Side.BACK).isPresent)
    }

    private fun getMarketReciprocal(latestPrices: List<MarketPrices>, side: Side): OptionalDouble {
        return latestPrices.first().getReciprocal(side)
    }

}