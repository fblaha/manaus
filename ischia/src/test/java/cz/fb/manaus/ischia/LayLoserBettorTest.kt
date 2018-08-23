package cz.fb.manaus.ischia

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.AbstractBettorTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*
import java.util.OptionalDouble.of

@ActiveProfiles("ischia")
class LayLoserBettorTest : AbstractBettorTest<LayLoserBettor>() {

    @Test
    fun `place bet - based on fairness`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.98, 3.2, of(3.05), 1)),
                3, OptionalDouble.of(2.88))
    }

    @Test
    fun `place bet - based on best price`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.58, 3.15, of(3.0), 1)),
                3, OptionalDouble.of(2.6))
    }

    @Test
    fun `place bet - based on last matched or traded volume`() {
        checkPlace(persistMarket(reactorTestFactory.createMarket(2.8, 3.2, of(2.5), 1)),
                3, OptionalDouble.of(2.48))
    }

    @Test
    fun `too close price for update`() {
        val market = persistMarket(reactorTestFactory.createMarket(2.90, 3.2, of(3.0), 1))

        checkUpdate(market, 2.86, Side.LAY, 0, 0)
        checkUpdate(market, 2.88, Side.LAY, 0, 0)

        checkUpdate(market, 2.9, Side.LAY, 0, 3)
        checkUpdate(market, 2.84, Side.LAY, 0, 3)
        checkUpdate(market, 2.92, Side.LAY, 0, 3)
        checkUpdate(market, 2.94, Side.LAY, 0, 3)
    }

}
