package cz.fb.manaus.core.maintanance

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Stopwatch
import cz.fb.manaus.core.repository.MarketPurger
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class MarketCleaner(
        @param:Value(HIST_DAYS_EL) private val marketHistoryDays: Long,
        private val marketPurger: MarketPurger,
        private val metricRegistry: MetricRegistry) : PeriodicMaintenanceTask {

    override val name: String = "marketCleanup"

    override val pausePeriod = Duration.ofMinutes(15)!!

    override fun execute(): ConfigUpdate {
        val stopwatch = Stopwatch.createUnstarted().start()
        // TODO delete bets and actions
        val count = marketPurger.purgeInactive(Instant.now().minus(6, ChronoUnit.HOURS))
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
