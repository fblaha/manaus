package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.repository.domain.Market
import org.springframework.stereotype.Component

@Component
class InPlayCategorizer : AbstractDelegatingCategorizer("inPlay_") {

    public override fun getCategoryRaw(market: Market): Set<String> {
        return setOf(market.inPlay.toString())
    }
}
