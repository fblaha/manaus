package cz.fb.manaus.core.category

import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class CategoryTest {

    @Test
    fun `parse base and tail`() {
        assertEquals(COUNTRY_CAT_BASE, Category.parse(MARKET_CZE).base)
        assertEquals(CZE, Category.parse(MARKET_CZE).tail)
        assertEquals(CZE, Category.parse(RAW).tail)
    }

    @Test
    fun `parse base and with complex tail`() {
        assertEquals(COUNTRY_CAT_BASE, Category.parse(MARKET_CZE + "_kl").base)
    }

    companion object {
        const val CZE = "cze"
        const val RAW = COUNTRY_PREFIX + CZE
        const val MARKET_CZE = Category.MARKET_PREFIX + RAW
        const val COUNTRY_CAT_BASE = Category.MARKET_PREFIX + COUNTRY_PREFIX
    }

}
