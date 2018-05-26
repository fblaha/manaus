package cz.fb.manaus.reactor.profit.progress;

import com.google.common.collect.ImmutableList;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class CoverageFunctionProfitService extends AbstractFunctionProfitService implements FunctionProfitRecordCalculator {

    public CoverageFunctionProfitService(List<ProgressFunction> functions) {
        super(functions);
    }

    public List<ProfitRecord> getProfitRecords(List<SettledBet> bets, Optional<String> funcName,
                                               double chargeRate, Optional<String> projection) {
        return getProfitRecords(this::getProfitRecords, bets, chargeRate, funcName, projection);
    }

    @Override
    public List<ProfitRecord> getProfitRecords(ProgressFunction function, List<SettledBet> bets,
                                               BetCoverage coverage, Map<String, Double> charges) {
        Predicate<SettledBet> coveredPredicate = bet -> coverage.isCovered(bet.getBetAction().getMarket().getId(),
                bet.getSelectionId());
        var coverSeparation = bets.stream()
                .collect(Collectors.partitioningBy(coveredPredicate));
        var covered = coverSeparation.get(true);
        var solo = coverSeparation.get(false);

        var growthSeparation = covered.stream()
                .collect(Collectors.partitioningBy(this::isChargeGrowth));
        var head = growthSeparation.get(true);
        var tail = growthSeparation.get(false);

        var builder = ImmutableList.<ProfitRecord>builder();

        addRecord("solo", solo, function, coverage, charges)
                .ifPresent(builder::add);
        addRecord("covered", covered, function, coverage, charges)
                .ifPresent(builder::add);
        addRecord("covHead", head, function, coverage, charges)
                .ifPresent(builder::add);
        addRecord("covTail", tail, function, coverage, charges)
                .ifPresent(builder::add);
        return builder.build();
    }

    private Optional<ProfitRecord> addRecord(String categoryName, List<SettledBet> bets, ProgressFunction function,
                                             BetCoverage coverage, Map<String, Double> charges) {
        var average = getAverage(bets, function);
        var category = getValueCategory(function.getName() + "_" + categoryName, average);
        if (average.isPresent()) {
            return Optional.of(computeFunctionRecord(category, bets.stream(), charges, coverage));
        }
        return Optional.empty();
    }

    private boolean isChargeGrowth(SettledBet bet) {
        var action = bet.getBetAction();
        var chargeGrowth = action.getDoubleProperty("chargeGrowth");
        return chargeGrowth.orElse(Double.MAX_VALUE) > 1;
    }

    private OptionalDouble getAverage(List<SettledBet> bets, ProgressFunction function) {
        return bets.stream()
                .map(function)
                .filter(OptionalDouble::isPresent)
                .mapToDouble(OptionalDouble::getAsDouble)
                .average();
    }

}
