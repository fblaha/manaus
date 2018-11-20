package cz.fb.manaus.core.maintanance.db

import com.codahale.metrics.MetricRegistry
import com.google.common.base.Stopwatch
import cz.fb.manaus.core.maintanance.ConfigUpdate
import cz.fb.manaus.core.maintanance.PeriodicMaintenanceTask
import cz.fb.manaus.core.repository.MarketFootprintLoader
import cz.fb.manaus.core.repository.MarketPurger
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger


@Component
@Profile(ManausProfiles.DB)
class MarketCleaner(
        private val marketRepository: MarketRepository,
        private val approvers: List<MarketDeletionApprover>,
        private val marketFootprintLoader: MarketFootprintLoader,
        private val marketPurger: MarketPurger,
        private val metricRegistry: MetricRegistry) : PeriodicMaintenanceTask {

    override val name: String = "marketCleanup"

    override val pausePeriod = Duration.ofMinutes(15)!!

    override fun execute(): ConfigUpdate {
        val stopwatch = Stopwatch.createUnstarted().start()
        var count = 0
        for ((from, to) in approvers.mapNotNull { it.timeRange }) {
            val footprints = marketRepository.find(from, to).map { marketFootprintLoader.toFootprint(it) }
            for (footprint in footprints) {
                if (approvers.any { it.isDeletable(footprint) }) {
                    log.log(Level.INFO, "Deleting market ''{0}'' ", footprint.market)
                    marketPurger.purge(footprint)
                    count++
                }
            }
        }

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
