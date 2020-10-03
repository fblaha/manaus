package cz.fb.manaus.core.maintanance.db

import cz.fb.manaus.core.batch.MarketFootprintLoader
import cz.fb.manaus.core.batch.MarketPurger
import cz.fb.manaus.core.maintanance.PeriodicTask
import cz.fb.manaus.core.model.MarketStatus
import cz.fb.manaus.core.repository.MarketRepository
import cz.fb.manaus.core.repository.Repository
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.logging.Logger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@Component
@ExperimentalTime
@Profile(ManausProfiles.DB)
class MarketStatusCleaner(
        private val repository: Repository<MarketStatus>
) : PeriodicTask {

    override val name: String = "marketStatusCleanup"

    override val pausePeriod = Duration.ofMinutes(60)!!

    private val log = Logger.getLogger(MarketStatusCleaner::class.simpleName)

    override fun execute() {
        val now = Instant.now()
        repository.list()
                .filter { it.openDate.isBefore(now) }
                .onEach { log.info { "deleting market status '${it.id}'" } }
                .forEach { repository.delete(it.id) }
    }

}
