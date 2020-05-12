package cz.fb.manaus.core.provider

import cz.fb.manaus.core.model.bfProvider
import cz.fb.manaus.core.model.mbProvider
import cz.fb.manaus.core.provider.ProviderTag.*
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class MockSelector(override val tags: Set<ProviderTag>) : ProviderSelector

class ExchangeProviderTest {

    @Test
    fun validate() {
        bfProvider.validate()
    }

    @Test
    fun `invalid tags`() {
        val invalid = mbProvider.copy(tags = setOf(PriceShiftContinuous, PriceShiftFixedStep))
        assertFailsWith<IllegalStateException> { invalid.validate() }
    }

    @Test
    fun `match tags`() {
        assertFalse { bfProvider.matches(MockSelector(setOf(VendorMatchbook))) }
        assertTrue { bfProvider.matches(MockSelector(setOf(VendorBetfair))) }
        assertTrue { bfProvider.matches(MockSelector(setOf(VendorBetfair, PriceShiftFixedStep))) }
        assertFalse { bfProvider.matches(MockSelector(setOf(VendorBetfair, PriceShiftContinuous))) }
    }
}