package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FairnessTest {

    @Test
    fun `more credible side`() {
        assertEquals(Side.BACK, Fairness(0.9, 1.2).moreCredibleSide.get())
        assertEquals(Side.LAY, Fairness(0.9, 1.05).moreCredibleSide.get())

        assertEquals(Side.BACK, Fairness(0.9, null).moreCredibleSide.get())
        assertEquals(Side.LAY, Fairness(null, 1.2).moreCredibleSide.get())

        assertFalse(Fairness(null, null).moreCredibleSide.isPresent)
    }
}
