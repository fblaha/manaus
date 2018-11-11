package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MatchedPriceCategorizer : RealizedBetCategorizer {

    @Autowired
    private lateinit var priceService: PriceService

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val matched = realizedBet.settledBet.price.price
        val requested = realizedBet.betAction.price.price
        val side = realizedBet.settledBet.price.side
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
