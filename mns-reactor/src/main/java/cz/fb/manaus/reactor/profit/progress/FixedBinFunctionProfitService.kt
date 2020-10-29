package cz.fb.manaus.reactor.profit.progress

import com.google.common.math.IntMath
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.apache.commons.math3.util.Precision
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class FixedBinFunctionProfitService(
        functions: List<ProgressFunction>,
        private val categoryService: CategoryService,
        private val profitService: ProfitService
) {
    private val functions: Map<String, ProgressFunction> = functions.map { it.name to it }.toMap()

    fun getProfitRecords(
            bets: List<RealizedBet>,
            funcName: String? = null,
            binCount: Int,
            projection: String? = null
    ): List<ProfitRecord> {

        val coverage = BetCoverage.from(bets)
        val filtered = when (projection) {
            null -> bets
            else -> categoryService.filterBets(bets, projection)
        }
        val functions = getProgressFunctions(funcName)
        return functions.flatMap { computeProfitRecords(it, binCount, coverage, filtered) }
    }


    private fun computeProfitRecords(
            function: ProgressFunction,
            binCount: Int,
            coverage: BetCoverage,
            bets: List<RealizedBet>
    ): List<ProfitRecord> {
        val computed = bets.map { it to function(it) }

        val (hasValue, noValues) = computed.partition { it.second != null }

        if (hasValue.isEmpty()) return emptyList()

        val sortedCopy = hasValue.sortedBy { it.second }

        val binSize = IntMath.divide(sortedCopy.size, binCount, RoundingMode.CEILING)

        val bins = sortedCopy.chunked(binSize)

        val result = bins.map { computeBinRecord(function.name, it, coverage) }

        if (noValues.isNotEmpty() && function.includeNoValues) {
            val noValBets = noValues.map { it.first }
            val category = "${function.name}: -"
            val noValRecord = computeFunctionRecord(category, noValBets, coverage)
            return listOf(noValRecord) + result
        }
        return result
    }


    private fun getProgressFunctions(funcName: String?): List<ProgressFunction> {
        return if (funcName != null) {
            listOf(functions.getValue(funcName))
        } else {
            functions.values.sortedBy { it.name }
        }
    }

    private fun computeBinRecord(
            name: String,
            bin: List<Pair<RealizedBet, Double?>>,
            coverage: BetCoverage
    ): ProfitRecord {
        val average = bin.mapNotNull { it.second }.average()
        val category = getValueCategory(name, average)
        val bets = bin.map { it.first }
        return computeFunctionRecord(category, bets, coverage)
    }

    private fun getValueCategory(name: String, average: Double?): String {
        return when (average) {
            null -> "$name: -"
            else -> "$name: ${Precision.round(average, 4)}"
        }
    }

    private fun computeFunctionRecord(
            category: String,
            bets: List<RealizedBet>,
            coverage: BetCoverage
    ): ProfitRecord {
        val binRecords = bets.map {
            profitService.toProfitRecord(it, category, it.settledBet.commission ?: 0.0, coverage)
        }
        return profitService.mergeCategory(category, binRecords)
    }

}
