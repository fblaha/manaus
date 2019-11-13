package cz.fb.manaus.reactor.betting.listener

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.betting.BetCommand
import cz.fb.manaus.reactor.betting.BetEvent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertTrue

@Component
class MockBetEventListener : BetEventListener {
    override val side: Side = Side.BACK

    override fun onBetEvent(event: BetEvent): List<BetCommand> {
        event.newPrice = Price(3.0, 3.0, Side.BACK)
        return listOf(BetCommand(betTemplate, event.betAction))
    }
}

val snapshot = MarketSnapshot(runnerPrices = runnerPrices, currentBets = emptyList(), market = market)

class BetEventExplorerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var betEventExplorer: BetEventExplorer


    @Test
    fun onMarketSnapshot() {
        val bets = betEventExplorer.onMarketSnapshot(MarketSnapshotEvent(snapshot, account))
        assertTrue { bets.isNotEmpty() }
        assertTrue { bets.all { it.action?.price == Price(3.0, 3.0, Side.BACK) } }
    }

}