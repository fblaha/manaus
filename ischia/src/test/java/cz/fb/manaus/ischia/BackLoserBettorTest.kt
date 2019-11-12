package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import cz.fb.manaus.reactor.betting.listener.BetCoordinator
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class BackLoserBettorTest : AbstractDatabaseTestCase() {

    @BackLoserBet
    @Autowired
    private lateinit var bettor: BetCoordinator
    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory

    private lateinit var bettorTester: BettorTester

    @Before
    fun setUp() {
        bettorTester = BettorTester(bettor, betActionRepository)
    }

    @Test
    fun `place bet - based on best price`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.98, 3.8, 3.0),
                3, 3.75)
    }

    @Test
    fun `place bet - based on fairness`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.98, 3.3, 3.0),
                3, 3.25)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        bettorTester.checkPlace(reactorTestFactory.newMarketPrices(2.98, 3.2, 4.0),
                3, 4.0)
    }

    @Test
    fun `too close price for update`() {
        val market = reactorTestFactory.newMarketPrices(2.8, 3.4, 3.0)
        bettorTester.checkUpdate(market, 3.4, Side.BACK, 0, 0)
        bettorTester.checkUpdate(market, 3.35, Side.BACK, 0, 0)
        bettorTester.checkUpdate(market, 3.5, Side.BACK, 0, 3)
        bettorTester.checkUpdate(market, 3.3, Side.BACK, 0, 3)
        bettorTester.checkUpdate(market, 3.25, Side.BACK, 0, 3)
    }

}
