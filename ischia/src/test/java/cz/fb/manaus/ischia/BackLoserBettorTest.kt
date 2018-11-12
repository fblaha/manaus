package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AbstractBettorTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("ischia")
class BackLoserBettorTest : AbstractBettorTest<BackLoserBettor>() {

    @Test
    fun `place bet - based on best price`() {
        checkPlace(persistMarket(reactorTestFactory.createMarketPrices(2.98, 3.8, 3.0)),
                3, 3.75)
    }

    @Test
    fun `place bet - based on fairness`() {
        checkPlace(persistMarket(reactorTestFactory.createMarketPrices(2.98, 3.3, 3.0)),
                3, 3.25)
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        checkPlace(persistMarket(reactorTestFactory.createMarketPrices(2.98, 3.2, 4.0)),
                3, 4.0)
    }

    @Test
    fun `too close price for update`() {
        val market = persistMarket(reactorTestFactory.createMarketPrices(2.8, 3.4, 3.0))
        checkUpdate(market, 3.4, Side.BACK, 0, 0)
        checkUpdate(market, 3.35, Side.BACK, 0, 0)
        checkUpdate(market, 3.5, Side.BACK, 0, 3)
        checkUpdate(market, 3.3, Side.BACK, 0, 3)
        checkUpdate(market, 3.25, Side.BACK, 0, 3)
    }

}
