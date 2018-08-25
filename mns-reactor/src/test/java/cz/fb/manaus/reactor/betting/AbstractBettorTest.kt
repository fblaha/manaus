package cz.fb.manaus.reactor.betting

import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.Maps
import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.CoreTestFactory.*
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.rounding.RoundingService
import org.apache.commons.lang3.time.DateUtils.addHours
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

abstract class AbstractBettorTest<T : AbstractUpdatingBettor> : AbstractDaoTest() {
    @Autowired
    protected lateinit var reactorTestFactory: ReactorTestFactory
    @Autowired
    private lateinit var coreTestFactory: CoreTestFactory
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var bettor: T

    private fun check(marketPrices: MarketPrices, bets: List<Bet>, placeCount: Int, updateCount: Int): BetCollector {
        val collector = BetCollector()
        val snapshot = MarketSnapshot.from(marketPrices, bets,
                Optional.of(createTradedVolume(marketPrices)))
        bettor.onMarketSnapshot(snapshot, collector, Optional.empty<AccountMoney>(), setOf())
        assertEquals(placeCount, collector.toPlace.size)
        assertEquals(updateCount, collector.toUpdate.size)
        return collector
    }

    private fun createTradedVolume(marketPrices: MarketPrices): Map<Long, TradedVolume> {
        val result = LinkedListMultimap.create<Long, Price>()
        for (runnerPrices in marketPrices.runnerPrices) {
            val lastMatchedPrice = runnerPrices.lastMatchedPrice
            result.put(runnerPrices.selectionId, Price(lastMatchedPrice, 5.0, null))
            result.put(runnerPrices.selectionId,
                    Price(roundingService.increment(lastMatchedPrice, 1).asDouble, 5.0, null))
            result.put(runnerPrices.selectionId,
                    Price(roundingService.decrement(lastMatchedPrice, 1).asDouble, 5.0, null))
        }
        return Maps.transformValues(result.asMap(), { TradedVolume(it) })
    }

    protected fun checkPlace(marketPrices: MarketPrices, expectedCount: Int, expectedPrice: OptionalDouble): BetCollector {
        val result = check(marketPrices, listOf(), expectedCount, 0)
        val toPlace = result.toPlace
        if (expectedPrice.isPresent) {
            for (command in toPlace) {
                assertEquals(expectedPrice.asDouble, command.bet.requestedPrice.price)
            }
        }
        return result
    }

    protected fun persistMarket(prices: MarketPrices): MarketPrices {
        marketDao.saveOrUpdate(prices.market)
        marketPricesDao.saveOrUpdate(prices)
        return prices
    }

    protected fun checkUpdate(marketPrices: MarketPrices, oldPrice: Double, type: Side, placeCount: Int, updateCount: Int) {
        val oldOne = Price(oldPrice, 3.72, type)
        val unmatchedHome = Bet(AbstractDaoTest.BET_ID, AbstractDaoTest.MARKET_ID, HOME, oldOne, PLACED_DATE, 0.0)
        val unmatchedDraw = Bet(AbstractDaoTest.BET_ID + 1, AbstractDaoTest.MARKET_ID, DRAW, oldOne, PLACED_DATE, 0.0)
        val unmatchedAway = Bet(AbstractDaoTest.BET_ID + 2, AbstractDaoTest.MARKET_ID, AWAY, oldOne, PLACED_DATE, 0.0)
        val bets = listOf(unmatchedHome, unmatchedDraw, unmatchedAway)
        val actions = bets.map { bet -> coreTestFactory.savePlaceAction(bet, marketPrices.market) }

        check(marketPrices, bets, placeCount, updateCount)
        actions.forEach { betActionDao.delete(it) }
    }

    companion object {
        var PLACED_DATE: Date = addHours(Date(), -10)
    }
}
