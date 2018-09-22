package cz.fb.manaus.reactor.profit.progress

import com.google.common.base.Joiner
import com.google.common.collect.Ordering.from
import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.profit.ProfitPlugin
import cz.fb.manaus.reactor.profit.ProfitService
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction
import org.apache.commons.math3.util.Precision
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.Comparator.comparing
import java.util.Objects.requireNonNull

abstract class AbstractFunctionProfitService(functions: List<ProgressFunction>) {
    private val functions: Map<String, ProgressFunction> = functions.map { it.name to it }.toMap()
    @Autowired
    private lateinit var profitPlugin: ProfitPlugin
    @Autowired
    private lateinit var categoryService: CategoryService
    @Autowired
    private lateinit var profitService: ProfitService


    protected fun getProfitRecords(calculator: FunctionProfitRecordCalculator, bets: List<SettledBet>,
                                   chargeRate: Double, funcName: Optional<String>, projection: Optional<String>): List<ProfitRecord> {
        var bets = bets
        val charges = profitPlugin.getCharges(bets, chargeRate)
        val coverage = BetCoverage.from(bets)

        if (projection.isPresent) {
            bets = categoryService.filterBets(bets, projection.get(), coverage)
        }

        val profitRecords = LinkedList<ProfitRecord>()
        for (function in getProgressFunctions(funcName)) {
            profitRecords.addAll(calculator.getProfitRecords(function, bets, coverage, charges))
        }
        return profitRecords
    }

    private fun getProgressFunctions(funcName: Optional<String>): Iterable<ProgressFunction> {
        return if (funcName.isPresent) {
            listOf(requireNonNull<ProgressFunction>(functions[funcName.get()],
                    String.format("No such function '%s'", funcName)))
        } else {
            from(comparing<ProgressFunction, String>({ it.name }))
                    .immutableSortedCopy(functions.values)
        }
    }

    protected fun computeChunkRecord(name: String, chunk: List<Pair<SettledBet, Double?>>,
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

    protected fun computeFunctionRecord(category: String, bets: List<SettledBet>,
                                        charges: Map<String, Double>, coverage: BetCoverage): ProfitRecord {
        val chunkRecords = bets.map { bet ->
            profitService.toProfitRecord(bet, category, charges[bet.betAction.betId]!!, coverage)
        }
        return profitService.mergeCategory(category, chunkRecords)
    }

}
