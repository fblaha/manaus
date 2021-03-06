package cz.fb.manaus.reactor.betting.listener

import com.google.common.primitives.Doubles
import cz.fb.manaus.core.model.RunnerPrices
import cz.fb.manaus.core.model.Side

class ProbabilityComparator(private val side: Side) : Comparator<RunnerPrices> {

    override fun compare(list1: RunnerPrices, list2: RunnerPrices): Int {
        val backList1 = list1.by(side)
        val backList2 = list2.by(side)
        val bestPrice1 = backList1.bestPrice ?: error("empty price list")
        val bestPrice2 = backList2.bestPrice ?: error("empty price list")
        val price1 = bestPrice1.price
        val price2 = bestPrice2.price
        return Doubles.compare(price1, price2)
    }

    companion object {
        var COMPARATORS = mapOf(
                Side.BACK to ProbabilityComparator(Side.BACK),
                Side.LAY to ProbabilityComparator(Side.LAY)
        )
    }

}

fun sortPrices(side: Side, marketPrices: List<RunnerPrices>): List<RunnerPrices> {
    val ordering = ProbabilityComparator.COMPARATORS[side] ?: error("no such side")
    val sortedPrices = marketPrices.sortedWith(ordering)
    check(
            sortedPrices.map { it.selectionId }.distinct().count() ==
                    sortedPrices.map { it.selectionId }.count()
    )
    return sortedPrices
}
