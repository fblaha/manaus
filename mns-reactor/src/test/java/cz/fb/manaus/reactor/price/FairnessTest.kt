package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FairnessTest {

    @Test
    fun `more credible side`() {
        assertEquals(Side.BACK, Fairness(back = 0.9, lay = 1.2).moreCredibleSide)
        assertEquals(Side.LAY, Fairness(back = 0.9, lay = 1.05).moreCredibleSide)

        assertEquals(Side.BACK, Fairness(back = 0.9).moreCredibleSide)
        assertEquals(Side.LAY, Fairness(lay = 1.2).moreCredibleSide)

        assertNull(Fairness().moreCredibleSide)
    }
}
