package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.spring.ManausProfiles
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsCollectionContaining.hasItem
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TheAbstractUnprofitableCategoriesRegistryTest : AbstractDatabaseTestCase() {

    @Autowired
    private lateinit var registry: TestUnprofitableCategoriesRegistry

    private fun pr(category: String, profitAndLoss: Double, betCount: Int): ProfitRecord {
        return ProfitRecord(category, profitAndLoss, betCount, 0, 2.0, 0.06)
    }

    @Before
    fun setUp() {
        registry.setWhitelist("white.tes")
    }

    @After
    fun tearDown() {
        registry.setWhitelist("white.tes")
    }

    @Test
    fun `blacklist threshold`() {
        assertTrue("horror" in registry.getBlacklist(0.1, 1, 110,
                listOf(pr("horror", -10.0, 10)).stream(), setOf()))
        assertFalse("horror" in registry.getBlacklist(0.1, 1, 90,
                listOf(pr("horror", -10.0, 10)).stream(),
                setOf()))
        assertFalse("horror" in registry.getBlacklist(0.1, 0, 110,
                listOf(pr("horror", -10.0, 10)).stream(),
                setOf()))
    }

    @Test
    fun `blacklist sort`() {
        assertThat(registry.getBlacklist(0.1, 1, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)).stream(), setOf()),
                allOf(hasItem("horror"), not(hasItem("weak")), not(hasItem("bad")))
        )
        assertThat(registry.getBlacklist(0.1, 2, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)).stream(), setOf()),
                allOf(hasItem("horror"), not(hasItem("weak")), hasItem("bad"))
        )
        assertThat(registry.getBlacklist(0.1, 3, 110,
                listOf(pr("horror", -10.0, 10), pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)).stream(), setOf()),
                allOf(hasItem("horror"), hasItem("weak"), hasItem("bad"))
        )
    }

    @Test
    fun `blacklist duplicate`() {
        assertThat(registry.getBlacklist(0.1, 2, 110,
                listOf(pr("horror", -10.0, 10),
                        pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)).stream(),
                setOf("horror")),
                allOf(not(hasItem("horror")), hasItem("weak"), hasItem("bad"))
        )
    }

    @Test
    fun `whitelist priority over blacklist`() {
        assertThat(registry.getBlacklist(0.1, 2, 110,
                listOf(pr("white.test", -10.0, 10),
                        pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)).stream(),
                setOf()),
                allOf(not(hasItem("white.test")), hasItem("weak"), hasItem("bad"))
        )
    }

    @Test
    fun `update filter prefix`() {
        val configUpdate = ConfigUpdate.empty(Duration.ZERO)
        val properties = configUpdate.setProperties
        registry.updateBlacklists(listOf(pr(MarketCategories.ALL, 10.0, 100),
                pr("weak1", -1.0, 5),
                pr("not_match", -1.0, 2),
                pr("weak2", -1.0, 5)), configUpdate)

        assertEquals("weak1,weak2", properties["unprofitable.black.list.test.5"])
    }

    @Test
    fun threshold() {
        assertEquals(0.1, registry.getThreshold(10))
    }

    @Test
    fun save() {
        val configUpdate = ConfigUpdate.empty(Duration.ZERO)
        registry.saveBlacklist(10, setOf("weak1", "weak2", "weak3"), configUpdate)
        val properties = configUpdate.setProperties

        assertEquals("weak1,weak2,weak3", properties["unprofitable.black.list.test.10"])
    }

    @Test
    fun `blacklist to save`() {
        val configUpdate = ConfigUpdate.empty(Duration.ZERO)
        val properties = configUpdate.setProperties
        registry.saveBlacklist(10, setOf("weak10_1", "weak10_2", "weak10_3"), configUpdate)
        assertEquals("weak10_1,weak10_2,weak10_3", properties["unprofitable.black.list.test.10"])
        registry.saveBlacklist(5, setOf("weak5_1", "weak5_2", "weak5_3"), configUpdate)
        assertEquals("weak5_1,weak5_2,weak5_3", properties["unprofitable.black.list.test.5"])
    }

    @Component
    @Profile(ManausProfiles.DB)
    private class TestUnprofitableCategoriesRegistry :
            AbstractUnprofitableCategoriesRegistry("test",
                    Duration.ofDays(30), Optional.of(Side.LAY), 0.0, "weak",
                    mapOf(5 to 2, 2 to 7))

}
