package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import cz.fb.manaus.reactor.betting.listener.MarketSnapshotCoordinator
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class LayLoserBettorTest : AbstractDatabaseTestCase() {

    @LayLoserBet
    @Autowired
    private lateinit var bettor: MarketSnapshotCoordinator
    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory

    private lateinit var bettorTester: BettorTester

    @Before
    fun setUp() {
        bettorTester = BettorTester(bettor, betActionRepository)
    }

    @Test
    fun `place bet - based on fairness`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.98, 3.2, 3.05),
                3, 2.88)
    }

    @Test
    fun `place bet - based on best price`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.58, 3.15, 3.0),
                3, 2.6)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.8, 3.2, 2.5),
                3, 2.48)
    }

    @Test
    fun `too close price for update`() {
        val market = reactorTestFactory.newMarketPrices(2.90, 3.2, 3.0)

        bettorTester.checkUpdate(market, 2.86, Side.LAY, 0, 0)
        bettorTester.checkUpdate(market, 2.88, Side.LAY, 0, 0)

        bettorTester.checkUpdate(market, 2.9, Side.LAY, 0, 3)
        bettorTester.checkUpdate(market, 2.84, Side.LAY, 0, 3)
        bettorTester.checkUpdate(market, 2.92, Side.LAY, 0, 3)
        bettorTester.checkUpdate(market, 2.94, Side.LAY, 0, 3)
    }

}
