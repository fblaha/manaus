package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class TypeCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    public override fun getCategoryRaw(market: Market): Set<String> {
        return setOf(market.type?.toLowerCase() ?: "unknown")

    }

    companion object {
        const val PREFIX = "type_"
    }
}
