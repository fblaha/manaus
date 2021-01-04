package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class BackLoserBettorTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var bettorTester: BettorTester

    @Test
    fun `place bet - based on best price`() {
        val marketPrices = PricesTestFactory.newMarketPrices(2.98, 3.8, 3.0)
        bettorTester.checkPlace(Side.BACK, marketPrices, 3, 3.772)
    }

    @Test
    fun `place bet - based on fairness`() {
        val marketPrices = PricesTestFactory.newMarketPrices(2.98, 3.1, 3.0)
        bettorTester.checkPlace(Side.BACK, marketPrices, 3, 3.151)
    }

    @Test
    fun `update bet`() {
        val market = PricesTestFactory.newMarketPrices(2.8, 3.4, 3.0)
        bettorTester.checkUpdate(Side.BACK, 3.0, market, 0, 3)
    }

}
