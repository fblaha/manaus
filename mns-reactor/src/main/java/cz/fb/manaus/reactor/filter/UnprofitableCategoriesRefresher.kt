package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.maintanance.PeriodicTask
import cz.fb.manaus.core.repository.BlacklistedCategoryRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class UnprofitableCategoriesRefresher(
        private val blacklistedCategoryRepository: BlacklistedCategoryRepository,
        private val registries: List<AbstractUnprofitableCategoriesRegistry>) : PeriodicTask {

    private val log = Logger.getLogger(UnprofitableCategoriesRefresher::class.simpleName)

    override val name: String = "unprofitableCategoriesRefresh"

    override val pausePeriod: Duration = Duration.ofHours(8)

    override fun execute() {
        val old = blacklistedCategoryRepository.list()
        val current = registries.flatMap { it.getBlacklist() }
        val currentNames = current.map { it.name }.toSet()
        old.filter { it.name !in currentNames }
                .onEach { log.info { "deleting blacklisted category '$it'" } }
                .forEach { blacklistedCategoryRepository.delete(it.name) }
        current.onEach { log.info { "saving blacklisted category '$it'" } }
                .forEach { blacklistedCategoryRepository.saveOrUpdate(it) }
    }

}
