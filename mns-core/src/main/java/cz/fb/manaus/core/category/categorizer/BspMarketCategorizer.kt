package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class BspMarketCategorizer : AbstractDelegatingCategorizer("bsp_") {

    public override fun getCategoryRaw(market: Market): Set<String> {
        return setOf(market.isBspMarket.toString())
    }
}
