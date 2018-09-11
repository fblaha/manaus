package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MatchedPriceCategorizer : SettledBetCategorizer {

    @Autowired
    private lateinit var priceService: PriceService

    override fun isSimulationSupported(): Boolean {
        return false
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val matched = settledBet.price.price
        val requested = settledBet.betAction.price.price
        val side = settledBet.price.side
        return setOf(getCategory(matched, requested, side))
    }

    internal fun getCategory(matched: Double, requested: Double, side: Side): String {
        return if (Price.priceEq(matched, requested)) {
            PREFIX + "equal"
        } else {
            val downgrade = priceService.isDowngrade(matched, requested, side)
            if (downgrade) {
                PREFIX + "better"
            } else {
                PREFIX + "worse"
            }
        }
    }

    companion object {
        const val PREFIX = "matchedPrice_"
    }
}
