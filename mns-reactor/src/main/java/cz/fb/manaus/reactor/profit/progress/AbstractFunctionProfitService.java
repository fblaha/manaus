package cz.fb.manaus.reactor.profit.progress;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Ordering.from;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;

abstract public class AbstractFunctionProfitService {
    private final Map<String, ProgressFunction> functions;
    @Autowired
    private ProfitPlugin profitPlugin;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProfitService profitService;


    public AbstractFunctionProfitService(List<ProgressFunction> functions) {
        this.functions = Maps.uniqueIndex(functions, ProgressFunction::getName);
    }

    protected List<ProfitRecord> getProfitRecords(FunctionProfitRecordCalculator calculator, List<SettledBet> bets,
                                                  double chargeRate, Optional<String> funcName, Optional<String> projection) {
        Map<String, Double> charges = profitPlugin.getCharges(bets, chargeRate);
        BetCoverage coverage = BetCoverage.from(bets);

        if (projection.isPresent()) {
            bets = categoryService.filterBets(bets, projection.get(), Optional.empty(), coverage);
        }

        List<ProfitRecord> profitRecords = new LinkedList<>();
        for (ProgressFunction function : getProgressFunctions(funcName)) {
            profitRecords.addAll(calculator.getProfitRecords(function, bets, coverage, charges));
        }
        return profitRecords;
    }

    private Iterable<ProgressFunction> getProgressFunctions(Optional<String> funcName) {
        Iterable<ProgressFunction> selectedFunctions;
        if (funcName.isPresent()) {
            selectedFunctions = singletonList(checkNotNull(functions.get(funcName.get()),
                    "No such function '%s'", funcName));
        } else {
            selectedFunctions = from(comparing(ProgressFunction::getName))
                    .immutableSortedCopy(functions.values());
        }
        return selectedFunctions;
    }

    protected ProfitRecord computeChunkRecord(String name, List<Pair<SettledBet, OptionalDouble>> chunk,
                                              Map<String, Double> charges, BetCoverage coverage) {
        OptionalDouble average = chunk.stream().map(Pair::getRight)
                .mapToDouble(OptionalDouble::getAsDouble).average();
        if (average.isPresent()) {
            String category = getValueCategory(name, average);
            List<SettledBet> bets = chunk.stream().map(Pair::getLeft).collect(Collectors.toList());
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
        List<ProfitRecord> chunkRecords = bets.map(bet -> {
            String betId = bet.getBetAction().getBetId();
            return profitService.toProfitRecord(bet, category, charges.get(betId), coverage);
        }).collect(Collectors.toList());
        return profitService.mergeProfitRecords(chunkRecords);
    }

}
