package cz.fb.manaus.core.provider

import cz.fb.manaus.core.model.bfProvider
import cz.fb.manaus.core.model.mbProvider
import cz.fb.manaus.core.provider.ProviderTag.VendorBetfair
import cz.fb.manaus.core.provider.ProviderTag.VendorMatchbook
import org.junit.jupiter.api.Test
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
        val invalid = mbProvider.copy(tags = setOf(VendorBetfair, VendorMatchbook))
        assertFailsWith<IllegalStateException> { invalid.validate() }
    }

    @Test
    fun `match tags`() {
        assertFalse { bfProvider.matches(MockSelector(setOf(VendorMatchbook))) }
        assertTrue { bfProvider.matches(MockSelector(setOf(VendorBetfair))) }
    }
}