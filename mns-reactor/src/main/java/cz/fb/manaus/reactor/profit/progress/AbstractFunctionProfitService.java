package cz.fb.manaus.reactor.profit.progress;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.CategoryService;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.profit.ProfitPlugin;
import cz.fb.manaus.reactor.profit.ProfitService;
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Ordering.from;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

abstract public class AbstractFunctionProfitService {
    private final Map<String, ProgressFunction> functions;
    @Autowired
    private ProfitPlugin profitPlugin;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProfitService profitService;


    public AbstractFunctionProfitService(List<ProgressFunction> functions) {
        this.functions = functions.stream().collect(toMap(ProgressFunction::getName, identity()));
    }

    protected List<ProfitRecord> getProfitRecords(FunctionProfitRecordCalculator calculator, List<SettledBet> bets,
                                                  double chargeRate, Optional<String> funcName, Optional<String> projection) {
        var charges = profitPlugin.getCharges(bets, chargeRate);
        var coverage = BetCoverage.from(bets);

        if (projection.isPresent()) {
            bets = categoryService.filterBets(bets, projection.get(), coverage);
        }

        var profitRecords = new LinkedList<ProfitRecord>();
        for (var function : getProgressFunctions(funcName)) {
            profitRecords.addAll(calculator.getProfitRecords(function, bets, coverage, charges));
        }
        return profitRecords;
    }

    private Iterable<ProgressFunction> getProgressFunctions(Optional<String> funcName) {
        Iterable<ProgressFunction> selectedFunctions;
        if (funcName.isPresent()) {
            selectedFunctions = List.of(requireNonNull(functions.get(funcName.get()),
                    String.format("No such function '%s'", funcName)));
        } else {
            selectedFunctions = from(comparing(ProgressFunction::getName))
                    .immutableSortedCopy(functions.values());
        }
        return selectedFunctions;
    }

    protected ProfitRecord computeChunkRecord(String name, List<Pair<SettledBet, OptionalDouble>> chunk,
                                              Map<String, Double> charges, BetCoverage coverage) {
        var average = chunk.stream().map(Pair::getRight)
                .mapToDouble(OptionalDouble::getAsDouble).average();
        if (average.isPresent()) {
            var category = getValueCategory(name, average);
            var bets = chunk.stream().map(Pair::getLeft).collect(Collectors.toList());
            return computeFunctionRecord(category, bets.stream(), charges, coverage);
        } else {
            throw new IllegalStateException();
        }
    }

    protected String getValueCategory(String name, OptionalDouble average) {
        if (average.isPresent()) {
            return Joiner.on(": ").join(name, Precision.round(average.getAsDouble(), 4));
        } else {
            return Joiner.on(": ").join(name, "-");
        }
    }

    protected ProfitRecord computeFunctionRecord(String category, Stream<SettledBet> bets,
                                                 Map<String, Double> charges, BetCoverage coverage) {
        var chunkRecords = bets.map(bet -> {
            var betId = bet.getBetAction().getBetId();
            return profitService.toProfitRecord(bet, category, charges.get(betId), coverage);
        }).collect(Collectors.toList());
        return profitService.mergeCategory(category, chunkRecords);
    }

}
