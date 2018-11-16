package cz.fb.manaus.reactor.categorizer


import com.google.common.base.CaseFormat
import com.google.common.base.Preconditions
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import java.util.*

@Component
class CoverageCategorizer : RealizedBetCategorizer {

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet, coverage: BetCoverage): Set<String> {
        val marketId = realizedBet.market.id
        val selectionId = realizedBet.settledBet.selectionId
        val sides = coverage.getSides(marketId, selectionId)
        Preconditions.checkState(sides.size > 0)
        val builder = mutableMapOf<Side, Double>()
        for (side in sides) {
            builder[side] = coverage.getAmount(marketId, selectionId, side)
        }
        val amounts = builder.toMap()
        return getCategories(realizedBet.settledBet.price.side, amounts)
    }

    internal fun getCategories(mySide: Side, amounts: Map<Side, Double>): Set<String> {
        val sideFormatted = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, mySide.name)
        if (EnumSet.of(mySide) == amounts.keys) {
            val soloSide = "solo$sideFormatted"
            return setOf(PREFIX + soloSide, PREFIX + "solo")
        } else if (EnumSet.of(mySide, mySide.opposite) == amounts.keys) {
            val builder = mutableSetOf<String>()
            builder.add(PREFIX + "both")
            builder.add(PREFIX + "both")
            builder.add(PREFIX + "both" + sideFormatted)
            when {
                Price.amountEq(amounts[Side.LAY]!!, amounts[Side.BACK]!!) -> builder.add(PREFIX + "bothEqual")
                amounts[Side.LAY]!! > amounts[Side.BACK]!! -> builder.add(PREFIX + "bothLayGt")
                amounts[Side.LAY]!! < amounts[Side.BACK]!! -> builder.add(PREFIX + "bothBackGt")
            }
            return builder.toSet()
        }
        throw IllegalStateException()
    }

    companion object {
        const val PREFIX = "coverage_"
    }
}
