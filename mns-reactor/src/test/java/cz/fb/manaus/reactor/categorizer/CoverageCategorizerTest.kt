package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.realizedBet
import cz.fb.manaus.core.model.replacePrice
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class CoverageCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: CoverageCategorizer

    @Test
    fun `category by sides`() {
        assertThat(categorizer.getCategories(Side.BACK, mapOf(Side.BACK to 2.0)), hasItems("coverage_soloBack", "coverage_solo"))
        assertThat(categorizer.getCategories(Side.LAY, mapOf(Side.LAY to 2.0)), hasItems("coverage_soloLay", "coverage_solo"))
        assertThat(categorizer.getCategories(Side.BACK, mapOf(Side.LAY to 2.0, Side.BACK to 2.0)), hasItem("coverage_both"))
    }

    @Test
    fun `comparison categories`() {
        assertThat(categorizer.getCategories(Side.BACK, mapOf(Side.LAY to 2.0, Side.BACK to 2.0)), hasItem("coverage_bothEqual"))
        assertThat(categorizer.getCategories(Side.BACK, mapOf(Side.LAY to 3.0, Side.BACK to 2.0)), hasItem("coverage_bothLayGt"))
        assertThat(categorizer.getCategories(Side.BACK, mapOf(Side.LAY to 3.0, Side.BACK to 4.0)), hasItem("coverage_bothBackGt"))
    }

    @Test(expected = IllegalStateException::class)
    fun `category no side`() {
        categorizer.getCategories(Side.BACK, emptyMap())
    }

    @Test(expected = IllegalStateException::class)
    fun `category no my side`() {
        categorizer.getCategories(Side.BACK, mapOf(Side.LAY to 2.0))
    }

    @Test
    fun `category solo lay`() {
        val bet = realizedBet.replacePrice(Price(2.0, 2.0, Side.LAY))
        val coverage = BetCoverage.from(listOf(bet))
        assertThat(categorizer.getCategories(bet, coverage), hasItems("coverage_soloLay", "coverage_solo"))
    }
}