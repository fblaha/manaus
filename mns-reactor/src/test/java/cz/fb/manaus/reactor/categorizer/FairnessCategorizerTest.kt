package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith

class FairnessCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: FairnessCategorizer

    @Test
    fun `low category`() {
        assertEquals(categorizer.getCategory(0.5), "fairness_0.50-0.60")
        assertEquals(categorizer.getCategory(0.15), "fairness_0.10-0.20")
        assertEquals(categorizer.getCategory(1.5), "fairness_1.00+")
    }

    @Test
    fun `high category`() {
        assertEquals(categorizer.getCategory(0.8), "fairness_0.80-0.85")
        assertEquals(categorizer.getCategory(0.87), "fairness_0.85-0.90")
    }

    @Test
    fun testCategoryNegative() {
        assertFailsWith<NullPointerException> { categorizer.getCategory(-1.5) }
    }

    @Test
    fun testCategory() {
        assertEquals(categorizer.getCategories(CoreTestFactory.newSettledBet(2.0, Side.LAY), BetCoverage.EMPTY),
                setOf("fairness_0.75-0.80"))
    }

}