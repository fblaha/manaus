package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class LayLoserBettorTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var factory: PricesTestFactory

    @Autowired
    private lateinit var bettorTester: BettorTester


    @Test
    fun `place bet - based on fairness`() {
        bettorTester.checkPlace(Side.LAY,
                factory.newMarketPrices(2.98, 3.2, 3.05),
                3, 2.88)
    }

    @Test
    fun `place bet - based on best price`() {
        bettorTester.checkPlace(
                Side.LAY,
                factory.newMarketPrices(2.58, 3.15, 3.0),
                3, 2.6)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        bettorTester.checkPlace(
                Side.LAY,
                factory.newMarketPrices(2.8, 3.2, 2.5),
                3, 2.48)
    }

    @Test
    fun `too close price for update`() {
        val market = factory.newMarketPrices(2.90, 3.2, 3.0)

        bettorTester.checkUpdate(Side.LAY, 2.86, market, 0, 0)
        bettorTester.checkUpdate(Side.LAY, 2.88, market, 0, 0)

        bettorTester.checkUpdate(Side.LAY, 2.9, market, 0, 3)
        bettorTester.checkUpdate(Side.LAY, 2.84, market, 0, 3)
        bettorTester.checkUpdate(Side.LAY, 2.92, market, 0, 3)
        bettorTester.checkUpdate(Side.LAY, 2.94, market, 0, 3)
    }

}
