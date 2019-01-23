package cz.fb.manaus.core.model

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.category.Category
import cz.fb.manaus.core.category.categorizer.COUNTRY_PREFIX
import org.junit.Test
import kotlin.test.assertEquals

class ProfitRecordTest {

    @Test
    fun `all predicate`() {
        val czeCat = Category.MARKET_PREFIX + COUNTRY_PREFIX + "cze"
        val cze = ProfitRecord(czeCat, 100.0, 2.0, 0.06, 1, 1)
        val all = ProfitRecord(MarketCategories.ALL, 100.0, 2.0, 0.06, 1, 1)
        assertEquals(1, listOf(cze, all).filter { ProfitRecord.isAllCategory(it) }.count())
        assertEquals(1, listOf(all).filter { ProfitRecord.isAllCategory(it) }.count())
        assertEquals(0, listOf(cze).filter { ProfitRecord.isAllCategory(it) }.count())
    }
}
