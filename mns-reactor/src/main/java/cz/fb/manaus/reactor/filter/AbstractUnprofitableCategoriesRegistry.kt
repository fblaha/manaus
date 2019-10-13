package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.repository.RealizedBetLoader
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.reactor.profit.ProfitService
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.util.logging.Logger


abstract class AbstractUnprofitableCategoriesRegistry(
        private val name: String,
        private val period: Duration,
        private val side: Side? = null,
        private val maximalProfit: Double,
        private val filterPrefix: String,
        private val thresholds: Map<Int, Int>) {

    @Autowired
    private lateinit var profitService: ProfitService
    @Autowired
    private lateinit var settledBetRepository: SettledBetRepository
    @Autowired
    private lateinit var realizedBetLoader: RealizedBetLoader

    private val log = Logger.getLogger(AbstractUnprofitableCategoriesRegistry::class.simpleName)

    private val logPrefix: String
        get() = "unprofitable registry '$name': "

    fun getBlacklist(): List<BlacklistedCategory> {
        log.info { logPrefix + "black list update started" }
        val now = Instant.now()
        val settledBets = settledBetRepository.find(now.minusSeconds(period.toSeconds()), now, side)
        if (settledBets.isEmpty()) return emptyList()
        val realizedBets = settledBets.map { realizedBetLoader.toRealizedBet(it) }
        val profitRecords = profitService.getProfitRecords(
                bets = realizedBets,
                projection = null,
                simulationAwareOnly = true)

        log.info { logPrefix + "updating registry '$name'" }
        return getBlacklist(profitRecords)
    }

    internal fun getBlacklist(profitRecords: List<ProfitRecord>): List<BlacklistedCategory> {
        val all = profitRecords.find { ProfitRecord.isAllCategory(it) }!!
        val filtered = getFiltered(profitRecords)
        val totalCount = all.totalCount

        val totalBlacklist = mutableSetOf<String>()
        val perThreshold = mutableListOf<List<BlacklistedCategory>>()
        for ((thresholdPct, blackCount) in thresholds) {
            val threshold = getThreshold(thresholdPct)
            val blacklist = getBlacklist(threshold, blackCount, totalCount, filtered, totalBlacklist)
            perThreshold.add(blacklist)
            totalBlacklist.addAll(blacklist.map { it.name })
        }
        val result = perThreshold.flatten()
        check(result.distinctBy { it.name }.size == result.size)
        return result
    }

    private fun getFiltered(profitRecords: List<ProfitRecord>): List<ProfitRecord> {
        return if (filterPrefix.isBlank()) {
            profitRecords
        } else {
            profitRecords.filter { it.category.startsWith(filterPrefix) }
        }
    }

    internal fun getThreshold(thresholdPct: Int): Double {
        return thresholdPct / 100.0
    }

    internal fun getBlacklist(threshold: Double,
                              blackCount: Int,
                              totalCount: Int,
                              profitRecords: List<ProfitRecord>,
                              actualBlacklist: Set<String>): List<BlacklistedCategory> {

        val sorted = profitRecords.asSequence()
                .filter { it.category !in actualBlacklist }
                .filter { it.totalCount.toDouble() / totalCount <= threshold }
                .sortedBy { it.profit }

        return sorted.takeWhile { it.profit < maximalProfit }
                .take(blackCount)
                .map { BlacklistedCategory(it.category, period, it.profit) }
                .toList()
    }
}
