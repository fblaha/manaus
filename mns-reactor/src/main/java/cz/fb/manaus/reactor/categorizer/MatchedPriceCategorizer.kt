package cz.fb.manaus.reactor.categorizer


import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.reactor.price.Pricing
import org.springframework.stereotype.Component

@Component
object MatchedPriceCategorizer : RealizedBetCategorizer {

    private const val PREFIX = "matchedPrice_"

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val matched = realizedBet.settledBet.price.price
        val requested = realizedBet.betAction.price.price
        val side = realizedBet.settledBet.price.side
        return setOf(getCategory(matched, requested, side))
    }

    internal fun getCategory(matched: Double, requested: Double, side: Side): String {
        return if (matched priceEq requested) {
            PREFIX + "equal"
        } else {
            val downgrade = Pricing.isDowngrade(matched, requested, side)
            if (downgrade) {
                PREFIX + "better"
            } else {
                PREFIX + "worse"
            }
        }
    }

}
