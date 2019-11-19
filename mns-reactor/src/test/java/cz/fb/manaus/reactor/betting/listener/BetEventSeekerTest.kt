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

    val mockEvent = market.event.copy(name = "mockEvent")

    override fun onBetEvent(event: BetEvent): BetCommand? {
        if (event.market.event === mockEvent) {
            event.newPrice = Price(3.0, 3.0, Side.BACK)
            return BetCommand(betTemplate, event.betAction)
        }
        return null
    }
}


class BetEventSeekerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var betEventSeeker: BetEventSeeker
    @Autowired
    private lateinit var listener: MockBetEventListener


    @Test
    fun onMarketSnapshot() {
        val snapshot = MarketSnapshot(
                runnerPrices = runnerPrices,
                currentBets = emptyList(),
                market = market.copy(event = listener.mockEvent)
        )
        val bets = betEventSeeker.onMarketSnapshot(MarketSnapshotEvent(snapshot, account))
        assertTrue { bets.isNotEmpty() }
        assertTrue { bets.all { it.action?.price == Price(3.0, 3.0, Side.BACK) } }
    }

}