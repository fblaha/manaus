package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
object TypeCategorizer : AbstractDelegatingCategorizer("type_") {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val type = market.type
        return when (type) {
            null -> emptySet()
            else -> setOf(market.type.toLowerCase())
        }
    }
}
