package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.model.provider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.spring.ManausProfiles.TEST
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(TEST, inheritProfiles = false)
class RateStepRoundingPluginTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var plugin: RateStepRoundingPlugin

    @Test
    fun `shift by 1`() {
        assertEquals(2.02, plugin.shift(2.0, 1), 0.0001)
    }

    @Test
    fun `inc 1_X prices`() {
        assertEquals(1.227, plugin.shift(1.222, 1), 0.001)
        assertEquals(1.128, plugin.shift(1.125, 1), 0.001)
        assertEquals(1.131, plugin.shift(1.128, 1), 0.001)
        assertEquals(1.13, plugin.shift(1.125, 2), 0.001)
        assertEquals(2.01, plugin.shift(1.99, 1), 0.001)
    }

    @Test
    fun `dec 2_X prices`() {
        assertEquals(1.98, plugin.shift(2.0, -1), 0.0001)
        assertEquals(2.764, plugin.shift(2.8, -1), 0.0001)
    }

    @Test
    fun `dec 3_X prices`() {
        assertEquals(2.96, plugin.shift(3.0, -1), 0.0001)
    }

    @Test
    fun `inc 3_X prices`() {
        assertEquals(3.55, plugin.shift(3.5, 1), 0.0001)
    }

    @Test
    fun `inc 5_X prices`() {
        assertEquals(5.08, plugin.shift(5.0, 1), 0.0001)
    }

    @Test
    fun `rounding - big range`() {
        var previous = -1.0
        var price = provider.minPrice
        while (price < 5) {
            val current = plugin.round(price)
            assertTrue(previous <= current)
            previous = current
            price += 0.001
        }
    }

}