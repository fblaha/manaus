package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

abstract class AbstractBettorTest<T : AbstractUpdatingBettor> : AbstractDatabaseTestCase() {
    @Autowired
    protected lateinit var reactorTestFactory: ReactorTestFactory
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var bettor: T

    private fun check(marketPrices: List<RunnerPrices>,
                      bets: List<Bet>,
                      placeCount: Int,
                      updateCount: Int): BetCollector {
        val collector = BetCollector()
        val snapshot = MarketSnapshot.from(marketPrices, market, bets, createTradedVolume(marketPrices))
        bettor.onMarketSnapshot(snapshot, collector, null, setOf())
        assertEquals(placeCount, collector.getToPlace().size)
        assertEquals(updateCount, collector.getToUpdate().size)
        return collector
    }

    protected fun checkPlace(marketPrices: List<RunnerPrices>,
                             expectedCount: Int,
                             expectedPrice: Double?): BetCollector {
        val result = check(marketPrices, listOf(), expectedCount, 0)
        val toPlace = result.getToPlace()
        if (expectedPrice != null) {
            for (command in toPlace) {
                assertEquals(expectedPrice, command.bet.requestedPrice.price)
            }
        }
        return result
    }


    protected fun checkUpdate(marketPrices: List<RunnerPrices>, oldPrice: Double, type: Side, placeCount: Int, updateCount: Int) {
        val oldOne = Price(oldPrice, 3.72, type)
        val minus10h = Instant.now().minus(10, ChronoUnit.HOURS)
        val unmatchedHome = Bet("1", market.id, SEL_HOME, oldOne, minus10h)
        val unmatchedDraw = Bet("2", market.id, SEL_DRAW, oldOne, minus10h)
        val unmatchedAway = Bet("3", market.id, SEL_AWAY, oldOne, minus10h)
        val bets = listOf(unmatchedHome, unmatchedDraw, unmatchedAway)
        bets.map {
            betActionRepository.save(betAction.copy(
                    selectionId = it.selectionId,
                    price = oldOne,
                    betId = it.betId))
        }
        check(marketPrices, bets, placeCount, updateCount)
        betActionRepository.delete(market.id)
    }


    private fun createTradedVolume(marketPrices: List<RunnerPrices>): Map<Long, TradedVolume> {
        val result = mutableMapOf<Long, MutableList<TradedAmount>>()
        for (runnerPrices in marketPrices) {
            val lastMatchedPrice = runnerPrices.lastMatchedPrice!!
            result.getOrPut(runnerPrices.selectionId) { mutableListOf() }
                    .add(TradedAmount(lastMatchedPrice, 5.0))
            result.getOrPut(runnerPrices.selectionId) { mutableListOf() }
                    .add(TradedAmount(roundingService.increment(lastMatchedPrice, 1)!!, 5.0))
            result.getOrPut(runnerPrices.selectionId) { mutableListOf() }
                    .add(TradedAmount(roundingService.decrement(lastMatchedPrice, 1)!!, 5.0))
        }
        return result.mapValues { TradedVolume(it.value) }

    }
}
