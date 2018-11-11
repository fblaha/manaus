package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TheOnlySelectionMatchedCategorizer : RealizedBetCategorizer {

    @Autowired
    private lateinit var selectionActualMatchedCategorizer: SelectionActualMatchedCategorizer
    @Autowired
    private lateinit var actualMatchedCategorizer: ActualMatchedCategorizer

    override val isMarketSnapshotRequired: Boolean = true

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val selectionMatched = selectionActualMatchedCategorizer.getAmount(realizedBet)
        val allMatched = actualMatchedCategorizer.getAmount(realizedBet)
        return if (selectionMatched != null && allMatched != null) {
            val theOnlyMatched = Price.amountEq(allMatched, selectionMatched)
            setOf("theOnlyMatched_$theOnlyMatched")
        } else {
            emptySet()
        }
    }
}
