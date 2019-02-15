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
import java.util.logging.Logger


@Component
@Profile(ManausProfiles.DB)
class MarketCleaner(private val marketRepository: MarketRepository,
                    private val marketFootprintLoader: MarketFootprintLoader,
                    private val marketPurger: MarketPurger,
                    private val metricRegistry: MetricRegistry,
                    private val approvers: List<MarketDeletionApprover>) : PeriodicMaintenanceTask {

    override val name: String = "marketCleanup"

    override val pausePeriod = Duration.ofMinutes(15)!!

    private val log = Logger.getLogger(MarketCleaner::class.simpleName)

    override fun execute(): ConfigUpdate {
        val stopwatch = Stopwatch.createUnstarted().start()
        var count = 0L
        for ((from, to) in approvers.mapNotNull { it.timeRange }) {
            val footprints = marketRepository.find(from, to).map { marketFootprintLoader.toFootprint(it) }
            for (footprint in footprints) {
                if (approvers.any { it.isDeletable(footprint) }) {
                    log.info { "deleting market '${footprint.market}'" }
                    marketPurger.purge(footprint)
                    count++
                }
            }
        }
        metricRegistry.counter("purge.market").inc(count)
        val elapsed = stopwatch.stop().elapsed(TimeUnit.SECONDS)
        log.info { "market deletion - '$count' obsolete markets removed in '$elapsed' seconds" }
        return ConfigUpdate.NOP
    }
}
