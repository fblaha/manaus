package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.betting.HOME_EVENT_BACK
import cz.fb.manaus.reactor.betting.HOME_EVENT_LAY
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FixedDowngradeStrategy {

    @Test
    fun `fixed strategy`() {
        val strategy = fixedDowngradeStrategy(Side.BACK, 0.01)
        assertEquals(0.01, strategy(HOME_EVENT_BACK))
        assertNull(strategy(HOME_EVENT_LAY))
    }

    @Test
    fun `fixed strategy - predicate`() {
        val strategy = fixedDowngradeStrategy(Side.BACK, 0.01) { false }
        assertNull(strategy(HOME_EVENT_LAY))
        assertNull(strategy(HOME_EVENT_BACK))
    }

    @Test
    fun combine() {
        val strategy = chain(
                fixedDowngradeStrategy(Side.BACK, 0.01),
                fixedDowngradeStrategy(Side.LAY, 0.02)
        )
        assertEquals(0.01, strategy(HOME_EVENT_BACK))
        assertEquals(0.02, strategy(HOME_EVENT_LAY))
    }

}