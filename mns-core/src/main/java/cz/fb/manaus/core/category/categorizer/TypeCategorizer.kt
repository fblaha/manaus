package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
object TypeCategorizer : AbstractDelegatingCategorizer("type_") {

    public override fun getCategoryRaw(market: Market): Set<String> {
        return when (market.type) {
            null -> emptySet()
            else -> setOf(market.type.lowercase())
        }
    }
}
