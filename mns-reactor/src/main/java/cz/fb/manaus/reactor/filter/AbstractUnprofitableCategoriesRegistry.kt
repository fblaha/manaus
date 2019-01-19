package cz.fb.manaus.reactor.filter

import com.google.common.base.Joiner
import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.repository.RealizedBetLoader
import cz.fb.manaus.core.repository.SettledBetRepository
import cz.fb.manaus.reactor.profit.ProfitService
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import java.util.*
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
    @Autowired
    private lateinit var provider: ExchangeProvider

    private val log = Logger.getLogger(AbstractUnprofitableCategoriesRegistry::class.simpleName)

    private val logPrefix: String
        get() = String.format("UNPROFITABLE_REGISTRY(%s): ", name)

    private val propertyPrefix: String
        get() = "$UNPROFITABLE_BLACK_LIST$name."

    fun updateBlacklists(configUpdate: ConfigUpdate) {
        log.info { logPrefix + "black list update started" }
        val now = Instant.now()
        val settledBets = settledBetRepository.find(now.minusSeconds(period.toSeconds()), now, side)
        if (settledBets.isEmpty()) return
        val realizedBets = settledBets.map { realizedBetLoader.toRealizedBet(it) }
        val chargeRate = provider.chargeRate
        val profitRecords = profitService.getProfitRecords(realizedBets, null, true, chargeRate)

        log.info { logPrefix + "updating registry '$name'" }
        updateBlacklists(profitRecords, configUpdate)
    }

    internal fun updateBlacklists(profitRecords: List<ProfitRecord>, configUpdate: ConfigUpdate) {
        val all = profitRecords.find { ProfitRecord.isAllCategory(it) }!!
        val filtered = getFiltered(profitRecords)
        val totalCount = all.totalCount

        configUpdate.deletePrefixes.add(propertyPrefix)

        val totalBlacklist = HashSet<String>()
        for ((thresholdPct, blackCount) in thresholds) {
            val threshold = getThreshold(thresholdPct)
            val blacklist = getBlacklist(threshold, blackCount, totalCount, filtered, totalBlacklist)
            totalBlacklist.addAll(blacklist)
            saveBlacklist(thresholdPct, blacklist, configUpdate)
        }
    }

    private fun getFiltered(profitRecords: List<ProfitRecord>): List<ProfitRecord> {
        return if (filterPrefix.isBlank()) {
            profitRecords
        } else {
            profitRecords.filter { it.category.startsWith(filterPrefix) }
        }
    }

    internal fun saveBlacklist(thresholdPct: Int, blacklist: Set<String>, configUpdate: ConfigUpdate) {
        if (!blacklist.isEmpty()) {
            configUpdate.setProperties[propertyPrefix + thresholdPct] = Joiner.on(',').join(TreeSet(blacklist))
        }
    }

    internal fun getThreshold(thresholdPct: Int): Double {
        return thresholdPct / 100.0
    }

    internal fun getBlacklist(threshold: Double,
                              blackCount: Int,
                              totalCount: Int,
                              profitRecords: List<ProfitRecord>,
                              blacklist: Set<String>): Set<String> {
        val currentBlacklist = mutableListOf<String>()

        val sorted = profitRecords
                .filter { it.totalCount.toDouble() / totalCount <= threshold }
                .sortedBy { it.profit }

        for (weak in sorted) {
            if (currentBlacklist.size >= blackCount || weak.profit >= maximalProfit) break
            if (weak.category !in blacklist) {
                currentBlacklist.add(weak.category)
            }
        }
        return currentBlacklist.toSet()
    }

    companion object {
        private const val UNPROFITABLE_BLACK_LIST = "unprofitable.black.list."
    }
}
