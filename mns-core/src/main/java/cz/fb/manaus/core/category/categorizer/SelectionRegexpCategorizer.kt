package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component

@Component
class SelectionRegexpCategorizer : AbstractRegexpResolver("selectionRegexp_"), SettledBetCategorizer {

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val selectionName = settledBet.selectionName
        return getCategories(selectionName)
    }

    internal fun getCategories(selectionName: String): Set<String> {
        val selectionBased = getCategories(selectionName, SELECTION_MAP)
        return selectionBased.map { this.addPrefix(it) }.toSet()
    }

    companion object {
        val SELECTION_MAP = mapOf(
                "draw" to compile("^The\\s+Draw$"),
                "yes" to compile("^Yes$"),
                "no" to compile("^No$"))
    }
}
