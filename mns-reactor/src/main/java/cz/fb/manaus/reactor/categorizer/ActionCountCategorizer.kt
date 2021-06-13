package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Market
import org.springframework.stereotype.Component

@Component
object ActionCountCategorizer : ActionHistoryCategorizer {

    override fun getCategories(actions: List<BetAction>, market: Market): Set<String> {
        return setOf("betActionCount_" + getCategory(actions.size))
    }

    private fun getCategory(actions: Int): String {
        return when (actions) {
            1 -> "1"
            2 -> "2"
            3 -> "3"
            4 -> "4"
            in 5..9 -> "5+"
            in 10..20 -> "10+"
            in 20..30 -> "20+"
            in 30..40 -> "30+"
            in 40..50 -> "40+"
            in 50..99 -> "50+"
            else -> "100+"
        }
    }
}
