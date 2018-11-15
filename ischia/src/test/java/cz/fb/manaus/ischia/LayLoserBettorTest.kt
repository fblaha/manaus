package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AbstractBettorTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class LayLoserBettorTest : AbstractBettorTest<LayLoserBettor>() {

    @Test
    fun `place bet - based on fairness`() {
        checkPlace(reactorTestFactory.createMarketPrices(2.98, 3.2, 3.05),
                3, 2.88)
    }

    @Test
    fun `place bet - based on best price`() {
        checkPlace(reactorTestFactory.createMarketPrices(2.58, 3.15, 3.0),
                3, 2.6)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        checkPlace(reactorTestFactory.createMarketPrices(2.8, 3.2, 2.5),
                3, 2.48)
    }

    @Test
    fun `too close price for update`() {
        val market = reactorTestFactory.createMarketPrices(2.90, 3.2, 3.0)

        checkUpdate(market, 2.86, Side.LAY, 0, 0)
        checkUpdate(market, 2.88, Side.LAY, 0, 0)

        checkUpdate(market, 2.9, Side.LAY, 0, 3)
        checkUpdate(market, 2.84, Side.LAY, 0, 3)
        checkUpdate(market, 2.92, Side.LAY, 0, 3)
        checkUpdate(market, 2.94, Side.LAY, 0, 3)
    }

}
