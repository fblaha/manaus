package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import org.junit.Test
import java.util.OptionalDouble.empty
import java.util.OptionalDouble.of
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FairnessTest {

    @Test
    fun `more credible side`() {
        assertEquals(Side.BACK, Fairness(of(0.9), of(1.2)).moreCredibleSide.get())
        assertEquals(Side.LAY, Fairness(of(0.9), of(1.05)).moreCredibleSide.get())

        assertEquals(Side.BACK, Fairness(of(0.9), empty()).moreCredibleSide.get())
        assertEquals(Side.LAY, Fairness(empty(), of(1.2)).moreCredibleSide.get())

        assertFalse(Fairness(empty(), empty()).moreCredibleSide.isPresent)
    }
}
