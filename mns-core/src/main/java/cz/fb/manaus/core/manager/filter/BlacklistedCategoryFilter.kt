package cz.fb.manaus.core.manager.filter

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.repository.BlacklistedCategoryRepository
import cz.fb.manaus.spring.ManausProfiles
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@Profile(ManausProfiles.DB)
class BlacklistedCategoryFilter(private val blacklistedCategoryRepository: BlacklistedCategoryRepository,
                                private val categoryService: CategoryService,
                                private val metricRegistry: MetricRegistry) : MarketFilter {

    private val log = Logger.getLogger(BlacklistedCategoryFilter::class.simpleName)

    override fun accept(market: Market): Boolean {
        val categories = categoryService.getMarketCategories(market, false)
        val blacklist = blacklistedCategoryRepository.list().map { it.name }.toSet()
        val intersection = categories intersect blacklist
        if (!intersection.isEmpty()) {
            metricRegistry.counter("blacklist.market").inc()
            log.info { "blacklist category '$intersection' for market '$market'" }
        }
        return intersection.isEmpty()
    }
}