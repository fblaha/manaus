package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AbstractBettorTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*
import java.util.OptionalDouble.of

@ActiveProfiles("ischia")
class BackLoserBettorTest : AbstractBettorTest<BackLoserBettor>() {

    @Test
    fun `place bet - based on best price`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98, 3.8, of(3.0), 1)),
                3, OptionalDouble.of(3.75))
    }

    @Test
    fun `place bet - based on fairness`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98, 3.3, of(3.0), 1)),
                3, OptionalDouble.of(3.25))
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98, 3.2, of(4.0), 1)),
                3, OptionalDouble.of(4.0))
    }

    @Test
    fun `too close price for update`() {
        val market = persistMarket(reactorTestFactory.createMarket(2.8, 3.4, of(3.0), 1))
        checkUpdate(market, 3.4, Side.BACK, 0, 0)
        checkUpdate(market, 3.35, Side.BACK, 0, 0)
        checkUpdate(market, 3.5, Side.BACK, 0, 3)
        checkUpdate(market, 3.3, Side.BACK, 0, 3)
        checkUpdate(market, 3.25, Side.BACK, 0, 3)
    }

}
