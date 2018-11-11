package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
class SelectionRegexpCategorizer : AbstractRegexpResolver("selectionRegexp_"), RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val selectionName = realizedBet.settledBet.selectionName
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
