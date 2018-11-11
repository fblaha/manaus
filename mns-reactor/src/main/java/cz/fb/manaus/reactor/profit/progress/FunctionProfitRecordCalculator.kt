package cz.fb.manaus.reactor.profit.progress

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction

interface FunctionProfitRecordCalculator {

    fun getProfitRecords(function: ProgressFunction,
                         bets: List<RealizedBet>,
                         coverage: BetCoverage,
                         charges: Map<String, Double>): List<ProfitRecord>

}
