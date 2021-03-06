package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.*
import cz.fb.manaus.reactor.betting.listener.BetEventSeeker
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Component
class BettorTester(
        private val betEventSeeker: BetEventSeeker,
) {

    private fun check(
            side: Side,
            marketPrices: List<RunnerPrices>,
            bets: List<TrackedBet>,
            expectedPlaceCount: Int,
            expectedUpdateCount: Int
    ): List<BetCommand> {

        val snapshot = MarketSnapshot(marketPrices, market, bets, emptyMap())
        val collected = betEventSeeker.onMarketSnapshot(MarketSnapshotEvent(snapshot, mbAccount))
                .filter { it.bet.remote.requestedPrice.side == side }
        assertEquals(expectedPlaceCount, collected.filter { it.place }.size)
        assertEquals(expectedUpdateCount, collected.filter { it.update }.size)
        return collected
    }

    fun checkPlace(
            side: Side,
            marketPrices: List<RunnerPrices>,
            expectedCount: Int,
            vararg expectedPrices: Double
    ) {
        val result = check(side, marketPrices, listOf(), expectedCount, 0)
        val toPlace = result.filter { it.place }
        val expected = expectedPrices.toSet()
        if (expected.isNotEmpty()) {
            toPlace.map { it.bet.remote.requestedPrice.price }.forEach { assertTrue("actual: $it") { it in expected } }
        }
    }

    fun checkUpdate(
            side: Side,
            oldPrice: Double,
            marketPrices: List<RunnerPrices>,
            expectedPlaceCount: Int,
            expectedUpdateCount: Int
    ) {
        val oldOne = Price(oldPrice, 3.72, side)
        val minus10h = Instant.now().minus(10, ChronoUnit.HOURS)
        val unmatchedHome = Bet("1", market.id, SEL_HOME, oldOne, minus10h)
        val unmatchedDraw = Bet("2", market.id, SEL_DRAW, oldOne, minus10h)
        val unmatchedAway = Bet("3", market.id, SEL_AWAY, oldOne, minus10h)
        val bets = listOf(unmatchedHome, unmatchedDraw, unmatchedAway)
                .map {
                    TrackedBet(
                            remote = it,
                            local = betAction.copy(
                                    selectionId = it.selectionId,
                                    price = oldOne,
                                    betId = it.betId
                            )
                    )
                }
        check(side, marketPrices, bets, expectedPlaceCount, expectedUpdateCount)
    }

}
