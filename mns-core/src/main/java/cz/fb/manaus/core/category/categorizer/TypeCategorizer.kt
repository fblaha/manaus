package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Component

@Component
class TypeCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val type = market.type
        return if (type == null) emptySet() else setOf(market.type.toLowerCase())
    }

    companion object {
        const val PREFIX = "type_"
    }
}
