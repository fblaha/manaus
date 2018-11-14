package cz.fb.manaus.reactor.profit.progress

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.springframework.stereotype.Component

@Component
class CoverageFunctionProfitService(functions: List<ProgressFunction>) : AbstractFunctionProfitService(functions), FunctionProfitRecordCalculator {

    fun getProfitRecords(bets: List<RealizedBet>, funcName: String?,
                         chargeRate: Double, projection: String? = null): List<ProfitRecord> {
        return getProfitRecords(this, bets, chargeRate, funcName, projection)
    }

    override fun getProfitRecords(function: ProgressFunction,
                                  bets: List<RealizedBet>,
                                  coverage: BetCoverage,
                                  charges: Map<String, Double>): List<ProfitRecord> {
        val (covered, solo) = bets.partition { coverage.isCovered(it.market.id, it.settledBet.selectionId) }

        val (head, tail) = covered.partition { this.isChargeGrowth(it) }

        val result = mutableListOf<ProfitRecord>()

        addRecord("solo", solo, function, coverage, charges)?.let { result.add(it) }
        addRecord("covered", covered, function, coverage, charges)?.let { result.add(it) }
        addRecord("covHead", head, function, coverage, charges)?.let { result.add(it) }
        addRecord("covTail", tail, function, coverage, charges)?.let { result.add(it) }
        return result
    }

    private fun addRecord(categoryName: String, bets: List<RealizedBet>, function: ProgressFunction,
                          coverage: BetCoverage, charges: Map<String, Double>): ProfitRecord? {
        val average = getAverage(bets, function)
        val category = getValueCategory(function.name + "_" + categoryName, average)
        return when (average) {
            null -> null
            else -> computeFunctionRecord(category, bets, charges, coverage)
        }
    }

    private fun isChargeGrowth(bet: RealizedBet): Boolean {
        val action = bet.betAction
        val chargeGrowth = action.properties["chargeGrowth"]
        return if (chargeGrowth != null) chargeGrowth.toDouble() > 1 else true
    }

    private fun getAverage(bets: List<RealizedBet>, function: ProgressFunction): Double? {
        return if (bets.isEmpty())
            return null
        else
            bets.mapNotNull { function(it) }
                    .average()
    }

}
