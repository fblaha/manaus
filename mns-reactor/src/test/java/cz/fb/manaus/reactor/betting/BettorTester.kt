package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.repository.BetActionRepository
import cz.fb.manaus.reactor.betting.listener.BetEventExplorer
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotEvent
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class BettorTester(
        private val bettor: BetEventExplorer,
        private val betActionRepository: BetActionRepository
) {

    private fun check(marketPrices: List<RunnerPrices>,
                      bets: List<Bet>,
                      placeCount: Int,
                      updateCount: Int): List<BetCommand> {
        val snapshot = MarketSnapshot(marketPrices, market, bets, emptyMap())
        val collected = bettor.onMarketSnapshot(MarketSnapshotEvent(snapshot, account))
        assertEquals(placeCount, collected.filter { it.isPlace }.size)
        assertEquals(updateCount, collected.filter { it.isUpdate }.size)
        return collected
    }

    fun checkPlace(marketPrices: List<RunnerPrices>,
                   expectedCount: Int,
                   expectedPrice: Double?) {
        val result = check(marketPrices, listOf(), expectedCount, 0)
        val toPlace = result.filter { it.isPlace }
        if (expectedPrice != null) {
            toPlace.forEach { assertEquals(expectedPrice, it.bet.requestedPrice.price) }
        }
    }

    fun checkUpdate(marketPrices: List<RunnerPrices>, oldPrice: Double, type: Side, placeCount: Int, updateCount: Int) {
        val oldOne = Price(oldPrice, 3.72, type)
        val minus10h = Instant.now().minus(10, ChronoUnit.HOURS)
        val unmatchedHome = Bet("1", market.id, SEL_HOME, oldOne, minus10h)
        val unmatchedDraw = Bet("2", market.id, SEL_DRAW, oldOne, minus10h)
        val unmatchedAway = Bet("3", market.id, SEL_AWAY, oldOne, minus10h)
        val bets = listOf(unmatchedHome, unmatchedDraw, unmatchedAway)
        bets.forEach {
            betActionRepository.idSafeSave(betAction.copy(
                    selectionId = it.selectionId,
                    price = oldOne,
                    betId = it.betId))
        }
        check(marketPrices, bets, placeCount, updateCount)
        betActionRepository.deleteByMarket(market.id)
    }

}
