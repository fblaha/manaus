package cz.fb.manaus.reactor.betting.strategy

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StrategyTest {

    @Test
    fun `fixed strategy`() {
        val strategy = fixedStrategy(Side.BACK, 0.01)
        assertEquals(0.01, strategy(HOME_EVENT_BACK))
        assertNull(strategy(HOME_EVENT_LAY))
    }

    @Test
    fun `fixed strategy - predicate`() {
        val strategy = fixedStrategy(Side.BACK, 0.01) { false }
        assertNull(strategy(HOME_EVENT_LAY))
        assertNull(strategy(HOME_EVENT_BACK))
    }

    @Test
    fun chain() {
        val strategy = chain(
            fixedStrategy(Side.BACK, 0.01),
            fixedStrategy(Side.LAY, 0.02)
        )
        assertEquals(0.01, strategy(HOME_EVENT_BACK))
        assertEquals(0.02, strategy(HOME_EVENT_LAY))
    }

}