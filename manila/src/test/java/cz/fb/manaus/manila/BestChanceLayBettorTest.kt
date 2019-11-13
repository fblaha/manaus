package cz.fb.manaus.manila

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.BettorTester
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("manila")
class BestChanceLayBettorTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory
    @Autowired
    private lateinit var bettorTester: BettorTester

    @Test
    fun `place bet positive`() {
        bettorTester.checkPlace(Side.LAY,
                reactorTestFactory.newMarketPrices(0.2, listOf(0.5, 0.3, 0.2)),
                1, 1.81)
        bettorTester.checkPlace(Side.LAY,
                reactorTestFactory.newMarketPrices(0.25, listOf(0.7, 0.2, 0.1)),
                1, 1.33)
    }

    @Test
    fun `no bet plced because to low proposed price`() {
        bettorTester.checkPlace(Side.LAY,
                reactorTestFactory.newMarketPrices(0.3, listOf(0.9, 0.05, 0.05)),
                0, null)
        bettorTester.checkPlace(Side.LAY,
                reactorTestFactory.newMarketPrices(0.3, listOf(0.7, 0.2, 0.1)),
                1, 1.31)
    }

}
