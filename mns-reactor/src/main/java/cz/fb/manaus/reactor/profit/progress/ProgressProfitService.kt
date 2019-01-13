package cz.fb.manaus.reactor.profit.progress

import com.google.common.math.IntMath
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class ProgressProfitService(functions: List<ProgressFunction>) : AbstractFunctionProfitService(functions) {

    fun getProfitRecords(bets: List<RealizedBet>, funcName: String? = null,
                         chunkCount: Int, chargeRate: Double, projection: String? = null): List<ProfitRecord> {
        val calculator = getCalculator(chunkCount)
        return getProfitRecords(calculator, bets, chargeRate, funcName, projection)
    }

    private fun getCalculator(chunkCount: Int): FunctionProfitRecordCalculator {
        return object : FunctionProfitRecordCalculator {
            override fun getProfitRecords(function: ProgressFunction, bets: List<RealizedBet>, coverage: BetCoverage, charges: Map<String, Double>): List<ProfitRecord> {
                return computeProfitRecords(function, chunkCount, coverage, bets, charges)
            }
        }
    }

    private fun computeProfitRecords(function: ProgressFunction,
                                     chunkCount: Int,
                                     coverage: BetCoverage,
                                     bets: List<RealizedBet>,
                                     charges: Map<String, Double>): List<ProfitRecord> {
        val computed = bets.map { Pair(it, function(it)) }

        val (hasValue, noValues) = computed.partition { it.second != null }

        val sortedCopy = hasValue.sortedBy { it.second }

        val chunkSize = IntMath.divide(sortedCopy.size, chunkCount, RoundingMode.CEILING)

        if (sortedCopy.isEmpty()) return emptyList()

        val chunks = sortedCopy.chunked(chunkSize)

        val result = chunks
                .map { computeChunkRecord(function.name, it, charges, coverage) }
                .toMutableList()


        if (noValues.isNotEmpty() && function.includeNoValues) {
            result.add(0, computeFunctionRecord(function.name + ": -",
                    noValues.map { it.first }, charges, coverage))
        }
        return result
    }


}
