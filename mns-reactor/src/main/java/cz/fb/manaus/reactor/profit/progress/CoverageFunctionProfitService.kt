package cz.fb.manaus.reactor.profit.progress

import com.google.common.collect.ImmutableList
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.springframework.stereotype.Component

@Component
class CoverageFunctionProfitService(functions: List<ProgressFunction>) : AbstractFunctionProfitService(functions), FunctionProfitRecordCalculator {

    fun getProfitRecords(bets: List<SettledBet>, funcName: String?,
                         chargeRate: Double, projection: String? = null): List<ProfitRecord> {
        return getProfitRecords(this, bets, chargeRate, funcName, projection)
    }

    override fun getProfitRecords(function: ProgressFunction, bets: List<SettledBet>,
                                  coverage: BetCoverage, charges: Map<String, Double>): List<ProfitRecord> {
        val (covered, solo) = bets.partition { bet ->
            coverage.isCovered(bet.betAction.market.id, bet.selectionId)
        }

        val (head, tail) = covered.partition { this.isChargeGrowth(it) }

        val builder = ImmutableList.builder<ProfitRecord>()

        addRecord("solo", solo, function, coverage, charges)?.let { builder.add(it) }
        addRecord("covered", covered, function, coverage, charges)?.let { builder.add(it) }
        addRecord("covHead", head, function, coverage, charges)?.let { builder.add(it) }
        addRecord("covTail", tail, function, coverage, charges)?.let { builder.add(it) }
        return builder.build()
    }

    private fun addRecord(categoryName: String, bets: List<SettledBet>, function: ProgressFunction,
                          coverage: BetCoverage, charges: Map<String, Double>): ProfitRecord? {
        val average = getAverage(bets, function)
        val category = getValueCategory(function.name + "_" + categoryName, average)
        return when (average) {
            null -> null
            else -> computeFunctionRecord(category, bets, charges, coverage)
        }
    }

    private fun isChargeGrowth(bet: SettledBet): Boolean {
        val action = bet.betAction
        val chargeGrowth = action.getDoubleProperty("chargeGrowth")
        return chargeGrowth.orElse(java.lang.Double.MAX_VALUE) > 1
    }

    private fun getAverage(bets: List<SettledBet>, function: ProgressFunction): Double? {
        return if (bets.isEmpty())
            return null
        else
            bets.mapNotNull { function(it) }
                    .average()
    }

}
