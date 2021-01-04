package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import cz.fb.manaus.reactor.PricesTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class LayLoserBettorTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var bettorTester: BettorTester


    @Test
    fun `place bet - based on fairness`() {
        val marketPrices = PricesTestFactory.newMarketPrices(2.98, 3.2, 3.05)
        bettorTester.checkPlace(Side.LAY, marketPrices, 3, 2.84)
    }

    @Test
    fun `place bet - based on best price`() {
        val marketPrices = PricesTestFactory.newMarketPrices(2.58, 3.15, 3.0)
        bettorTester.checkPlace(Side.LAY, marketPrices, 3, 2.596)
    }

    @Test
    fun `upadte bet`() {
        val market = PricesTestFactory.newMarketPrices(2.90, 3.2, 3.0)
        bettorTester.checkUpdate(Side.LAY, 3.0, market, 0, 3)
    }

}
