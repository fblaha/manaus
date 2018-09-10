package cz.fb.manaus.reactor.categorizer


import com.google.common.base.CaseFormat
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Component
import java.util.*

@Component
class CoverageCategorizer : SettledBetCategorizer {

    override fun isSimulationSupported(): Boolean {
        return false
    }

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val marketId = settledBet.betAction.market.id
        val selectionId = settledBet.selectionId
        val sides = coverage.getSides(marketId, selectionId)
        Preconditions.checkState(sides.size > 0)
        val builder = ImmutableMap.builder<Side, Double>()
        for (side in sides) {
            builder.put(side, coverage.getAmount(marketId, selectionId, side))
        }
        val amounts = builder.build()
        return getCategories(settledBet.price.side, amounts)
    }

    internal fun getCategories(mySide: Side, amounts: Map<Side, Double>): Set<String> {
        val sideFormatted = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, mySide.name)
        if (EnumSet.of(mySide) == amounts.keys) {
            val soloSide = "solo$sideFormatted"
            return setOf(PREFIX + soloSide, PREFIX + "solo")
        } else if (EnumSet.of(mySide, mySide.opposite) == amounts.keys) {
            val builder = ImmutableSet.builder<String>().add(PREFIX + "both")
            builder.add(PREFIX + "both")
            builder.add(PREFIX + "both" + sideFormatted)
            when {
                Price.amountEq(amounts[Side.LAY]!!, amounts[Side.BACK]!!) -> builder.add(PREFIX + "bothEqual")
                amounts[Side.LAY]!! > amounts[Side.BACK]!! -> builder.add(PREFIX + "bothLayGt")
                amounts[Side.LAY]!! < amounts[Side.BACK]!! -> builder.add(PREFIX + "bothBackGt")
            }
            return builder.build()
        }
        throw IllegalStateException()
    }

    companion object {
        const val PREFIX = "coverage_"
    }

}
