package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TheOnlySelectionMatchedCategorizer : SettledBetCategorizer {

    @Autowired
    private lateinit var selectionActualMatchedCategorizer: SelectionActualMatchedCategorizer
    @Autowired
    private lateinit var actualMatchedCategorizer: ActualMatchedCategorizer

    override fun isMarketSnapshotRequired(): Boolean {
        return true
    }

    override fun getCategories(bet: SettledBet, coverage: BetCoverage): Set<String> {
        val selectionMatched = selectionActualMatchedCategorizer.getAmount(bet)
        val allMatched = actualMatchedCategorizer.getAmount(bet)
        return if (selectionMatched != null && allMatched != null) {
            val theOnlyMatched = Price.amountEq(allMatched, selectionMatched)
            setOf("theOnlyMatched_$theOnlyMatched")
        } else {
            emptySet()
        }
    }
}
