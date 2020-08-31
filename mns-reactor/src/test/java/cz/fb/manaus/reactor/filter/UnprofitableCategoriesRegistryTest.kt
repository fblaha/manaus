package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.batch.RealizedBetLoader
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import cz.fb.manaus.reactor.profit.ProfitService
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnprofitableCategoriesRegistryTest : AbstractDatabaseTestCase() {

    private lateinit var registry: UnprofitableCategoriesRegistry

    @Autowired
    private lateinit var profitService: ProfitService

    @Autowired
    private lateinit var realizedBetLoader: RealizedBetLoader

    @Before
    fun setUp() {
        registry = UnprofitableCategoriesRegistry(
                name = "test",
                period = Duration.ofDays(30),
                side = Side.LAY,
                maximalProfit = 0.0,
                filterPrefix = "weak",
                thresholds = mapOf(5 to 2, 2 to 7),
                profitService = profitService,
                settledBetRepository = settledBetRepository,
                realizedBetLoader = realizedBetLoader
        )
    }

    private fun pr(category: String, profitAndLoss: Double, betCount: Int): ProfitRecord {
        return ProfitRecord(category, profitAndLoss, 2.0, 0.06, betCount, 0)
    }


    @Test
    fun `blacklist threshold`() {
        assertTrue {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 1,
                    totalCount = 110,
                    profitRecords = listOf(pr("horror", -10.0, 10)),
                    actualBlacklist = setOf()).map { it.name }
        }
        assertFalse {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 1,
                    totalCount = 90,
                    profitRecords = listOf(pr("horror", -10.0, 10)),
                    actualBlacklist = setOf()).map { it.name }
        }
        assertFalse {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 0,
                    totalCount = 110,
                    profitRecords = listOf(pr("horror", -10.0, 10)),
                    actualBlacklist = setOf()).map { it.name }
        }
    }

    @Test
    fun `blacklist sort`() {
        var blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 1,
                totalCount = 110,
                profitRecords = listOf(
                        pr("horror", -10.0, 10),
                        pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)
                ),
                actualBlacklist = emptySet()).map { it.name }
        assertTrue { "horror" in blacklist }
        assertFalse { "weak" in blacklist }
        assertFalse { "bad" in blacklist }
        blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 2,
                totalCount = 110,
                profitRecords = listOf(
                        pr("horror", -10.0, 10),
                        pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)
                ),
                actualBlacklist = emptySet()).map { it.name }
        assertTrue { "horror" in blacklist }
        assertFalse { "weak" in blacklist }
        assertTrue { "bad" in blacklist }
        blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 3,
                totalCount = 110,
                profitRecords = listOf(
                        pr("horror", -10.0, 10),
                        pr("weak", -1.0, 10),
                        pr("bad", -5.0, 10)
                ),
                actualBlacklist = setOf()).map { it.name }
        assertTrue { "horror" in blacklist }
        assertTrue { "weak" in blacklist }
        assertTrue { "bad" in blacklist }
    }

    @Test
    fun `blacklist duplicate`() {
        val records = listOf(
                pr("horror", -10.0, 10),
                pr("weak", -1.0, 10),
                pr("bad", -5.0, 10)
        )
        val blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 2,
                totalCount = 110,
                profitRecords = records,
                actualBlacklist = setOf("horror")
        ).map { it.name }.toSet()
        assertFalse { "horror" in blacklist }
        assertTrue { "weak" in blacklist }
        assertTrue { "bad" in blacklist }
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

}
