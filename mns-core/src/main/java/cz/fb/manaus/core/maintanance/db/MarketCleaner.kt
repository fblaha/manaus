package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.batch.MarketFootprintLoader
import cz.fb.manaus.core.batch.MarketPurger
import cz.fb.manaus.core.maintanance.PeriodicTask
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@Component
@ExperimentalTime
@Profile(ManausProfiles.DB)
class MarketCleaner(
        private val marketRepository: MarketRepository,
        private val marketFootprintLoader: MarketFootprintLoader,
        private val marketPurger: MarketPurger,
        private val approvers: List<MarketDeletionApprover>
) : PeriodicTask {

    override val name: String = "marketCleanup"

    override val pausePeriod = Duration.ofMinutes(15)!!

    private val log = Logger.getLogger(MarketCleaner::class.simpleName)

    override fun execute() {
        val (count, duration) = measureTimedValue {
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
            Metrics.counter("mns_market_purge").increment(count.toDouble())
            count
        }
        log.info { "market deletion - '${count}' obsolete markets removed in '${duration}'" }
    }
}
