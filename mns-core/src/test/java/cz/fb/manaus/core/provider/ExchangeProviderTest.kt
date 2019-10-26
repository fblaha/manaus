package cz.fb.manaus.core.provider

import cz.fb.manaus.core.model.provider
import cz.fb.manaus.core.provider.ProviderTag.*
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class MockSelector(override val tags: Set<ProviderTag>) : ProviderSelector

class ExchangeProviderTest {

    @Test
    fun validate() {
        provider.validate()
    }

    @Test
    fun `invalid tags`() {
        val invalid = provider.copy(tags = setOf(PriceShiftContinuous, PriceShiftFixedStep))
        assertFailsWith<IllegalStateException> { invalid.validate() }
    }

    @Test
    fun `match tags`() {
        assertFalse { provider.matches(MockSelector(setOf(VendorMatchbook))) }
        assertTrue { provider.matches(MockSelector(setOf(VendorBetfair))) }
        assertTrue { provider.matches(MockSelector(setOf(VendorBetfair, PriceShiftFixedStep))) }
        assertFalse { provider.matches(MockSelector(setOf(VendorBetfair, PriceShiftContinuous))) }
    }
}