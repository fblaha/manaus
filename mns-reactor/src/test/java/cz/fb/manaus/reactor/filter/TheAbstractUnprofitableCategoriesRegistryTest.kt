package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.spring.ManausProfiles
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TheAbstractUnprofitableCategoriesRegistryTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var registry: TestUnprofitableCategoriesRegistry

    private fun pr(category: String, profitAndLoss: Double, betCount: Int): ProfitRecord {
        return ProfitRecord(category, profitAndLoss, 2.0, 0.06, betCount, 0)
    }


    @Test
    fun `blacklist threshold`() {
        assertTrue("horror" in registry.getBlacklist(0.1, 1, 110,
                listOf(pr("horror", -10.0, 10)), setOf()).map { it.name })
        assertFalse("horror" in registry.getBlacklist(0.1, 1, 90,
                listOf(pr("horror", -10.0, 10)), setOf()).map { it.name })
        assertFalse("horror" in registry.getBlacklist(0.1, 0, 110,
                listOf(pr("horror", -10.0, 10)), setOf()).map { it.name })
    }

    @Test
    fun `blacklist sort`() {
        assertThat(registry.getBlacklist(0.1, 1, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)), setOf()).map { it.name },
                allOf(hasItem("horror"), not(hasItem("weak")), not(hasItem("bad")))
        )
        assertThat(registry.getBlacklist(0.1, 2, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)), setOf()).map { it.name },
                allOf(hasItem("horror"), not(hasItem("weak")), hasItem("bad"))
        )
        assertThat(registry.getBlacklist(0.1, 3, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)), setOf()).map { it.name },
                allOf(hasItem("horror"), hasItem("weak"), hasItem("bad"))
        )
    }

    @Test
    fun `blacklist duplicate`() {
        val records = listOf(pr("horror", -10.0, 10),
                pr("weak", -1.0, 10),
                pr("bad", -5.0, 10))
        val blacklist = registry.getBlacklist(0.1, 2, 110,
                records,
                setOf("horror"))
        assertThat(blacklist.map { it.name },
                allOf(not(hasItem("horror")), hasItem("weak"), hasItem("bad"))
        )
    }

    @Test
    fun `update filter prefix`() {
        val blacklist = registry.getBlacklist(listOf(
                pr(MarketCategories.ALL, 10.0, 100),
                pr("weak1", -1.0, 5),
                pr("not_match", -1.0, 2),
                pr("weak2", -1.0, 5)))

        assertEquals(listOf("weak1", "weak2"), blacklist.map { it.name }.toList())
    }

    @Test
    fun threshold() {
        assertEquals(0.1, registry.getThreshold(10))
    }

    @Component
    @Profile(ManausProfiles.DB)
    private class TestUnprofitableCategoriesRegistry :
            AbstractUnprofitableCategoriesRegistry("test",
                    Duration.ofDays(30), Side.LAY, 0.0, "weak",
                    mapOf(5 to 2, 2 to 7))

}
