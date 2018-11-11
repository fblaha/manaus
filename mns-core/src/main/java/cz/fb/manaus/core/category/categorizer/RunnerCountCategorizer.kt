package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
class RunnerCountCategorizer : AbstractDelegatingCategorizer(PREFIX) {

    public override fun getCategoryRaw(market: Market): Set<String> {
        val size = market.runners.map { it.selectionId }.distinct().count()
        return setOf(size.toString())
    }

    companion object {
        const val PREFIX = "runnerCount_"
    }
}
