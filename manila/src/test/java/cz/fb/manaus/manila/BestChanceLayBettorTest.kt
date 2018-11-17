package cz.fb.manaus.manila

import cz.fb.manaus.reactor.betting.AbstractBettorTest
import org.junit.Test
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("manila")
class BestChanceLayBettorTest : AbstractBettorTest<BestChanceLayBettor>() {

    @Test
    fun `place bet positive`() {
        checkPlace(reactorTestFactory.newMarketPrices(0.2, listOf(0.5, 0.3, 0.2)),
                1, 1.81)
        checkPlace(reactorTestFactory.newMarketPrices(0.25, listOf(0.7, 0.2, 0.1)),
                1, 1.33)
    }

    @Test
    fun `no bet plced because to low proposed price`() {
        checkPlace(reactorTestFactory.newMarketPrices(0.3, listOf(0.9, 0.05, 0.05)),
                0, null)
        checkPlace(reactorTestFactory.newMarketPrices(0.3, listOf(0.7, 0.2, 0.1)),
                1, 1.31)
    }

}
