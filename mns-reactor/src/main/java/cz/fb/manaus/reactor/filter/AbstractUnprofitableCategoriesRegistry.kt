package cz.fb.manaus.reactor.filter

import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.google.common.base.Strings
import cz.fb.manaus.core.dao.BetActionDao
import cz.fb.manaus.core.dao.SettledBetDao
import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.profit.ProfitService
import org.apache.commons.lang3.time.DateUtils.addDays
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.time.Duration
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Stream


abstract class AbstractUnprofitableCategoriesRegistry protected constructor(
        private val name: String, private val period: Duration,
        private val side: Optional<Side>,
        private val maximalProfit: Double,
        private val filterPrefix: String,
        private val thresholds: Map<Int, Int>) {

    @Autowired
    private lateinit var profitService: ProfitService
    @Autowired
    private lateinit var settledBetDao: SettledBetDao
    @Autowired
    private lateinit var betActionDao: BetActionDao
    @Autowired
    private lateinit var provider: ExchangeProvider

    private lateinit var whitelist: List<String>

    private val logPrefix: String
        get() = String.format("UNPROFITABLE_REGISTRY(%s): ", name)

    private val propertyPrefix: String
        get() = "$UNPROFITABLE_BLACK_LIST$name."

    @Autowired
    fun setWhitelist(@Value("#{systemEnvironment['MNS_CATEGORY_WHITE_LIST']}") rawWhiteList: String?) {
        this.whitelist = Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(Strings.nullToEmpty(rawWhiteList))
    }

    fun updateBlacklists(configUpdate: ConfigUpdate) {
        log.log(Level.INFO, logPrefix + "black list update started")
        val now = Date()
        val settledBets = settledBetDao.getSettledBets(
                Optional.of(addDays(now, -period.toDays().toInt())), Optional.of(now), side,
                OptionalInt.empty())
        betActionDao.fetchMarketPrices(settledBets.stream().map { it.betAction })
        if (settledBets.isEmpty()) return
        val chargeRate = provider.chargeRate
        val profitRecords = profitService.getProfitRecords(settledBets, Optional.empty(), true, chargeRate)

        log.log(Level.INFO, logPrefix + "updating registry ''{0}''", name)
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
            val blacklist = getBlacklist(threshold, blackCount, totalCount, filtered.stream(), totalBlacklist)
            totalBlacklist.addAll(blacklist)
            saveBlacklist(thresholdPct, blacklist, configUpdate)
        }
    }

    private fun getFiltered(profitRecords: List<ProfitRecord>): List<ProfitRecord> {
        return if (Strings.isNullOrEmpty(filterPrefix)) {
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

    internal fun getBlacklist(threshold: Double, blackCount: Int, totalCount: Int, profitRecords: Stream<ProfitRecord>,
                              blacklist: Set<String>): Set<String> {
        val currentBlacklist = LinkedHashSet<String>()

        val sorted = profitRecords.filter { record -> record.totalCount.toDouble() / totalCount <= threshold }
                .sorted(compareBy { it.profit })


        var i = 0
        for (weak in sorted) {
            if (i >= blackCount || weak.profit >= maximalProfit) break
            if (blacklist.contains(weak.category)) continue
            if (whitelist.stream().anyMatch { prefix -> weak.category.startsWith(prefix) }) continue
            currentBlacklist.add(weak.category)
            i++
        }
        return currentBlacklist
    }

    companion object {

        private const val UNPROFITABLE_BLACK_LIST = "unprofitable.black.list."
        private val log = Logger.getLogger(AbstractUnprofitableCategoriesRegistry::class.java.simpleName)
    }
}
