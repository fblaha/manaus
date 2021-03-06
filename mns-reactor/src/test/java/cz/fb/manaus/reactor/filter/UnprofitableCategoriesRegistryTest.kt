package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.MarketCategories
import cz.fb.manaus.core.batch.RealizedBetLoader
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import cz.fb.manaus.reactor.profit.ProfitService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnprofitableCategoriesRegistryTest : AbstractIntegrationTestCase() {

    private val pr = ProfitRecord(
            category = "",
            theoreticalProfit = -10.0,
            avgPrice = 2.0,
            charge = 0.06,
            layCount = 10,
            backCount = 0
    )

    private lateinit var registry: UnprofitableCategoriesRegistry

    @Autowired
    private lateinit var profitService: ProfitService

    @Autowired
    private lateinit var realizedBetLoader: RealizedBetLoader

    @BeforeEach
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

    @Test
    fun `blacklist threshold`() {
        assertTrue {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 1,
                    totalCount = 110,
                    profitRecords = listOf(pr.copy(category = "horror")),
                    actualBlacklist = setOf()
            ).map { it.name }
        }
        assertFalse {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 1,
                    totalCount = 90,
                    profitRecords = listOf(pr.copy(category = "horror")),
                    actualBlacklist = setOf()
            ).map { it.name }
        }
        assertFalse {
            "horror" in registry.getBlacklist(
                    threshold = 0.1,
                    blackCount = 0,
                    totalCount = 110,
                    profitRecords = listOf(pr.copy(category = "horror")),
                    actualBlacklist = setOf()
            ).map { it.name }
        }
    }

    @Test
    fun `blacklist sort`() {
        val records = listOf(
                pr.copy(category = "horror", theoreticalProfit = -10.0),
                pr.copy(category = "weak", theoreticalProfit = -1.0),
                pr.copy(category = "bad", theoreticalProfit = -5.0)
        )
        var blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 1,
                totalCount = 110,
                profitRecords = records,
                actualBlacklist = emptySet()
        ).map { it.name }
        assertTrue { "horror" in blacklist }
        assertFalse { "weak" in blacklist }
        assertFalse { "bad" in blacklist }
        blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 2,
                totalCount = 110,
                profitRecords = records,
                actualBlacklist = emptySet()
        ).map { it.name }
        assertTrue { "horror" in blacklist }
        assertFalse { "weak" in blacklist }
        assertTrue { "bad" in blacklist }
        blacklist = registry.getBlacklist(
                threshold = 0.1,
                blackCount = 3,
                totalCount = 110,
                profitRecords = records,
                actualBlacklist = setOf()
        ).map { it.name }
        assertTrue { "horror" in blacklist }
        assertTrue { "weak" in blacklist }
        assertTrue { "bad" in blacklist }
    }

    @Test
    fun `blacklist duplicate`() {
        val records = listOf(
                pr.copy(category = "horror", theoreticalProfit = -10.0),
                pr.copy(category = "weak", theoreticalProfit = -1.0),
                pr.copy(category = "bad", theoreticalProfit = -5.0)
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
        val blacklist = registry.getBlacklist(
                listOf(
                        pr.copy(category = MarketCategories.ALL, theoreticalProfit = 10.0, layCount = 100),
                        pr.copy(category = "weak1", theoreticalProfit = -1.0, layCount = 5),
                        pr.copy(category = "not_match", theoreticalProfit = -1.0, layCount = 2),
                        pr.copy(category = "weak2", theoreticalProfit = -1.0, layCount = 5)
                )
        )

        assertEquals(listOf("weak1", "weak2"), blacklist.map { it.name }.toList())
    }

    @Test
    fun threshold() {
        assertEquals(0.1, registry.getThreshold(10))
    }

}
