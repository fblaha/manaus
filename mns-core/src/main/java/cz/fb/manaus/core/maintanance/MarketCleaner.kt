package cz.fb.manaus.core.maintanance

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Stopwatch
import cz.fb.manaus.core.dao.MarketDao
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date.from
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Repository
@Profile(ManausProfiles.DB)
class MarketCleaner @Autowired
constructor(@param:Value(HIST_DAYS_EL) private val marketHistoryDays: Int) : PeriodicMaintenanceTask {
    @Autowired
    private lateinit var marketDao: MarketDao
    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    override val name: String = "marketCleanup"

    override val pausePeriod = Duration.ofMinutes(15)!!

    override fun execute(): ConfigUpdate {
        val stopwatch = Stopwatch.createUnstarted().start()
        val count = marketDao.deleteMarkets(from(Instant.now().minus(140, ChronoUnit.DAYS)))
        metricRegistry.counter("purge.market").inc(count.toLong())
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.log(Level.INFO, "DELETE_MARKETS: ''{0}'' obsolete markets removed in ''{1}'' seconds", arrayOf(count, elapsed))
        return ConfigUpdate.NOP
    }

    companion object {
        private val log = Logger.getLogger(MarketCleaner::class.java.simpleName)
        const val HIST_DAYS_EL = "#{systemEnvironment['MNS_MARKET_HISTORY_DAYS'] ?: 200}"
    }
}
