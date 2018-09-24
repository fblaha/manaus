package cz.fb.manaus.reactor.profit.progress

import com.google.common.collect.Lists
import com.google.common.math.IntMath
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Component
class ProgressProfitService @Autowired
constructor(functions: List<ProgressFunction>) : AbstractFunctionProfitService(functions) {

    fun getProfitRecords(bets: List<SettledBet>, funcName: String?,
                         chunkCount: Int, chargeRate: Double, projection: String?): List<ProfitRecord> {
        val calculator = getCalculator(chunkCount)
        return getProfitRecords(calculator, bets, chargeRate, funcName, projection)
    }

    private fun getCalculator(chunkCount: Int): FunctionProfitRecordCalculator {
        return object : FunctionProfitRecordCalculator {
            override fun getProfitRecords(function: ProgressFunction, bets: List<SettledBet>, coverage: BetCoverage, charges: Map<String, Double>): List<ProfitRecord> {
                return computeProfitRecords(function, chunkCount, coverage, bets, charges)
            }
        }
    }

    private fun computeProfitRecords(function: ProgressFunction, chunkCount: Int, coverage: BetCoverage,
                                     bets: List<SettledBet>, charges: Map<String, Double>): List<ProfitRecord> {
        val computed = bets.map { bet -> Pair(bet, function(bet)) }

        val (hasValue, noValues) = computed.partition { p -> p.second != null }

        val sortedCopy = hasValue.sortedBy { it.second }

        val chunkSize = IntMath.divide(sortedCopy.size, chunkCount, RoundingMode.CEILING)

        if (sortedCopy.isEmpty()) return emptyList()

        val chunks = Lists.partition<Pair<SettledBet, Double?>>(sortedCopy, chunkSize)

        // TODO parallel stream was here
        val result = chunks
                .map { chunk -> computeChunkRecord(function.name, chunk, charges, coverage) }
                .toMutableList()


        if (!noValues.isEmpty()) {
            result.add(0, computeFunctionRecord(function.name + ": -",
                    noValues.map { it.first }, charges, coverage))
        }
        return result
    }


}
