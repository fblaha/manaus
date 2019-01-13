package cz.fb.manaus.reactor.profit.progress

import com.google.common.base.Joiner
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.ProfitPlugin
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.apache.commons.math3.util.Precision
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractFunctionProfitService(functions: List<ProgressFunction>) {
    private val functions: Map<String, ProgressFunction> = functions.map { it.name to it }.toMap()
    @Autowired
    private lateinit var profitPlugin: ProfitPlugin
    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var profitService: ProfitService


    protected fun getProfitRecords(calculator: FunctionProfitRecordCalculator, bets: List<RealizedBet>,
                                   chargeRate: Double, funcName: String?, projection: String?): List<ProfitRecord> {
        val charges = profitPlugin.getCharges(bets, chargeRate)
        val coverage = BetCoverage.from(bets)

        val filtered = if (projection != null) categoryService.filterBets(bets, projection, coverage) else bets

        return getProgressFunctions(funcName)
                .flatMap { calculator.getProfitRecords(it, filtered, coverage, charges) }
    }

    private fun getProgressFunctions(funcName: String?): Iterable<ProgressFunction> {
        return if (funcName != null) {
            listOf(functions[funcName]!!)
        } else {
            functions.values.sortedBy { it.name }
        }
    }

    protected fun computeChunkRecord(name: String, chunk: List<Pair<RealizedBet, Double?>>,
                                     charges: Map<String, Double>, coverage: BetCoverage): ProfitRecord {
        val average = chunk.mapNotNull { it.second }.average()
        val category = getValueCategory(name, average)
        val bets = chunk.map { it.first }
        return computeFunctionRecord(category, bets, charges, coverage)
    }

    protected fun getValueCategory(name: String, average: Double?): String {
        return when (average) {
            null -> Joiner.on(": ").join(name, "-")
            else -> Joiner.on(": ").join(name, Precision.round(average, 4))
        }
    }

    protected fun computeFunctionRecord(category: String, bets: List<RealizedBet>,
                                        charges: Map<String, Double>, coverage: BetCoverage): ProfitRecord {
        val chunkRecords = bets.map {
            profitService.toProfitRecord(it, category, charges[it.settledBet.id]!!, coverage)
        }
        return profitService.mergeCategory(category, chunkRecords)
    }

}
