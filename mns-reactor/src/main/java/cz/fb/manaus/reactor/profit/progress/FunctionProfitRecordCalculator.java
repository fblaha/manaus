package cz.fb.manaus.reactor.profit.progress;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction;

import java.util.List;
import java.util.Map;

public interface FunctionProfitRecordCalculator {

    List<ProfitRecord> getProfitRecords(ProgressFunction function, List<SettledBet> bets,
                                        BetCoverage coverage, Map<String, Double> charges);

}
