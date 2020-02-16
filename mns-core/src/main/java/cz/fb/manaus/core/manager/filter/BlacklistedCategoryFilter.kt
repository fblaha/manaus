package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.repository.BlacklistedCategoryRepository
import cz.fb.manaus.spring.ManausProfiles
import io.micrometer.core.instrument.Metrics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class BlacklistedCategoryFilter(
        private val blacklistedCategoryRepository: BlacklistedCategoryRepository,
        private val categoryService: CategoryService
) : FreshMarketFilter {

    private val log = Logger.getLogger(BlacklistedCategoryFilter::class.simpleName)

    override fun accept(market: Market): Boolean {
        val categories = categoryService.getMarketCategories(market, false)
        val blacklist = blacklistedCategoryRepository.list().map { it.name }.toSet()
        val intersection = categories intersect blacklist
        if (intersection.isNotEmpty()) {
            Metrics.counter("mns_blacklist_market").increment()
            log.info { "blacklist category '$intersection' for market '$market'" }
        }
        return intersection.isEmpty()
    }
}