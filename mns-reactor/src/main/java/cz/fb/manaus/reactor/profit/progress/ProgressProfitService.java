package cz.fb.manaus.reactor.profit.progress;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.profit.progress.function.ProgressFunction;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;

@Component
public class ProgressProfitService extends AbstractFunctionProfitService {

    @Autowired
    public ProgressProfitService(List<ProgressFunction> functions) {
        super(functions);
    }

    public List<ProfitRecord> getProfitRecords(List<SettledBet> bets, Optional<String> funcName,
                                               int chunkCount, double chargeRate, Optional<String> projection) {
        FunctionProfitRecordCalculator calculator = getCalculator(chunkCount);
        return getProfitRecords(calculator, bets, chargeRate, funcName, projection);
    }

    public FunctionProfitRecordCalculator getCalculator(int chunkCount) {
        return (function, bets, coverage, charges) ->
                computeProfitRecords(function, chunkCount, coverage, bets, charges);
    }

    private List<ProfitRecord> computeProfitRecords(ProgressFunction function, int chunkCount, BetCoverage coverage,
                                                    List<SettledBet> bets, Map<String, Double> charges) {
        List<Pair<SettledBet, OptionalDouble>> computed = bets.stream()
                .map(bet -> new ImmutablePair<>(bet, function.function(bet)))
                .collect(Collectors.toList());

        List<Pair<SettledBet, OptionalDouble>> withValues = computed.stream()
                .filter(p -> p.getRight().isPresent())
                .collect(Collectors.toList());
        List<Pair<SettledBet, OptionalDouble>> noValues = computed.stream()
                .filter(p -> !p.getRight().isPresent())
                .collect(Collectors.toList());

        Pair<SettledBet, OptionalDouble>[] array = toArray(withValues);
        Arrays.parallelSort(array, comparingDouble(pair -> pair.getRight().getAsDouble()));
        ImmutableList<Pair<SettledBet, OptionalDouble>> sortedCopy = ImmutableList.copyOf(array);
        int chunkSize = IntMath.divide(sortedCopy.size(), chunkCount, RoundingMode.CEILING);

        if (sortedCopy.isEmpty()) return Collections.emptyList();

        List<List<Pair<SettledBet, OptionalDouble>>> chunks = Lists.partition(sortedCopy, chunkSize);

        LinkedList<ProfitRecord> result = chunks.parallelStream()
                .map(chunk -> computeChunkRecord(function.getName(), chunk, charges, coverage))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));

        if (!noValues.isEmpty()) {
            result.addFirst(computeFunctionRecord(function.getName() + ": -",
                    noValues.stream().map(Pair::getLeft), charges, coverage));
        }
        return result;
    }

    private Pair<SettledBet, OptionalDouble>[] toArray(List<Pair<SettledBet, OptionalDouble>> bets) {
        return bets.toArray((Pair<SettledBet, OptionalDouble>[]) Array.newInstance(Pair.class, bets.size()));
    }

}
