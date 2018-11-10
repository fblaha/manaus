package cz.fb.manaus.core.manager.filter

import com.codahale.metrics.MetricRegistry
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Component
import java.util.logging.Level
import java.util.logging.Logger

@Component
class CategoryBlacklistFilter(private val categoryService: CategoryService,
                              private val metricRegistry: MetricRegistry) : MarketFilter {


    override fun accept(market: Market, categoryBlacklist: Set<String>): Boolean {
        val categories = categoryService.getMarketCategories(market, false)
        val intersection = categories intersect categoryBlacklist
        if (!intersection.isEmpty()) {
            metricRegistry.counter("blacklist.market").inc()
            log.log(Level.INFO, "blacklist category ''{0}'' for market ''{1}''",
                    arrayOf(intersection, market))
        }
        return intersection.isEmpty()
    }

    companion object {
        private val log = Logger.getLogger(CategoryBlacklistFilter::class.java.simpleName)
    }
}