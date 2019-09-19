package cz.fb.manaus.reactor.profit.progress

import com.google.common.math.IntMath
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.ProfitPlugin
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.apache.commons.math3.util.Precision
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class FixedBinFunctionProfitService(functions: List<ProgressFunction>) {
    private val functions: Map<String, ProgressFunction> = functions.map { it.name to it }.toMap()
    @Autowired
    private lateinit var profitPlugin: ProfitPlugin
    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var profitService: ProfitService

    fun getProfitRecords(bets: List<RealizedBet>,
                         funcName: String? = null,
                         binCount: Int,
                         chargeRate: Double,
                         projection: String? = null): List<ProfitRecord> {

        val charges = profitPlugin.getCharges(bets, chargeRate)
        val coverage = BetCoverage.from(bets)
        val filtered = if (projection != null) categoryService.filterBets(bets, projection, coverage) else bets
        val functions = getProgressFunctions(funcName)
        return functions.flatMap { computeProfitRecords(it, binCount, coverage, filtered, charges) }
    }


    private fun computeProfitRecords(function: ProgressFunction,
                                     binCount: Int,
                                     coverage: BetCoverage,
                                     bets: List<RealizedBet>,
                                     charges: Map<String, Double>): List<ProfitRecord> {
        val computed = bets.map { it to function(it) }

        val (hasValue, noValues) = computed.partition { it.second != null }

        val sortedCopy = hasValue.sortedBy { it.second }

        val binSize = IntMath.divide(sortedCopy.size, binCount, RoundingMode.CEILING)

        if (sortedCopy.isEmpty()) return emptyList()

        val bins = sortedCopy.chunked(binSize)

        val result = bins.map { computeBinRecord(function.name, it, charges, coverage) }

        if (noValues.isNotEmpty() && function.includeNoValues) {
            val noValBets = noValues.map { it.first }
            val category = "${function.name}: -"
            val noValRecord = computeFunctionRecord(category, noValBets, charges, coverage)
            return listOf(noValRecord) + result
        }
        return result
    }


    private fun getProgressFunctions(funcName: String?): List<ProgressFunction> {
        return if (funcName != null) {
            listOf(functions[funcName]!!)
        } else {
            functions.values.sortedBy { it.name }
        }
    }

    private fun computeBinRecord(name: String,
                                 bin: List<Pair<RealizedBet, Double?>>,
                                 charges: Map<String, Double>,
                                 coverage: BetCoverage): ProfitRecord {
        val average = bin.mapNotNull { it.second }.average()
        val category = getValueCategory(name, average)
        val bets = bin.map { it.first }
        return computeFunctionRecord(category, bets, charges, coverage)
    }

    private fun getValueCategory(name: String, average: Double?): String {
        return when (average) {
            null -> "$name: -"
            else -> "$name: ${Precision.round(average, 4)}"
        }
    }

    private fun computeFunctionRecord(category: String,
                                      bets: List<RealizedBet>,
                                      charges: Map<String, Double>,
                                      coverage: BetCoverage): ProfitRecord {
        val binRecords = bets.map {
            profitService.toProfitRecord(it, category, charges[it.settledBet.id]!!, coverage)
        }
        return profitService.mergeCategory(category, binRecords)
    }

}
