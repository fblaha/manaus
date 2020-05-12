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
                3, 2.826, 2.846)
    }

    @Test
    fun `place bet - based on best price`() {
        bettorTester.checkPlace(
                Side.LAY,
                factory.newMarketPrices(2.58, 3.15, 3.0),
                3, 2.612)
    }

    @Test
    fun `too close price for update`() {
        val market = factory.newMarketPrices(2.90, 3.2, 3.0)

        bettorTester.checkUpdate(Side.LAY, 2.84, market, 0, 0)
        bettorTester.checkUpdate(Side.LAY, 2.82, market, 0, 0)

        bettorTester.checkUpdate(Side.LAY, 2.9, market, 0, 3)
        bettorTester.checkUpdate(Side.LAY, 2.78, market, 0, 3)
    }

}
