package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class BackLoserBettorTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory
    @Autowired
    private lateinit var bettorTester: BettorTester

    @Test
    fun `place bet - based on best price`() {
        bettorTester.checkPlace(
                Side.BACK,
                reactorTestFactory.newMarketPrices(2.98, 3.8, 3.0),
                3,
                3.75
        )
    }

    @Test
    fun `place bet - based on fairness`() {
        bettorTester.checkPlace(
                Side.BACK,
                reactorTestFactory.newMarketPrices(2.98, 3.3, 3.0),
                3, 3.25)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        bettorTester.checkPlace(
                Side.BACK,
                reactorTestFactory.newMarketPrices(2.98, 3.2, 4.0),
                3,
                4.0)
    }

    @Test
    fun `too close price for update`() {
        val market = reactorTestFactory.newMarketPrices(2.8, 3.4, 3.0)
        bettorTester.checkUpdate(Side.BACK, 3.4, market, 0, 0)
        bettorTester.checkUpdate(Side.BACK, 3.35, market, 0, 0)
        bettorTester.checkUpdate(Side.BACK, 3.5, market, 0, 3)
        bettorTester.checkUpdate(Side.BACK, 3.3, market, 0, 3)
        bettorTester.checkUpdate(Side.BACK, 3.25, market, 0, 3)
    }

}
