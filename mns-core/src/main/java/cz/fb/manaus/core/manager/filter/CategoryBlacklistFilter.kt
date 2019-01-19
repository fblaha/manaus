package cz.fb.manaus.core.manager.filter

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class CategoryBlacklistFilter(private val categoryService: CategoryService,
                              private val metricRegistry: MetricRegistry) : MarketFilter {

    private val log = Logger.getLogger(CategoryBlacklistFilter::class.simpleName)

    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val categories = categoryService.getMarketCategories(market, false)
        val intersection = categories intersect categoryBlacklist
        if (!intersection.isEmpty()) {
            metricRegistry.counter("blacklist.market").inc()
            log.info { "blacklist category '$intersection' for market '$market'" }
        }
        return intersection.isEmpty()
    }
}