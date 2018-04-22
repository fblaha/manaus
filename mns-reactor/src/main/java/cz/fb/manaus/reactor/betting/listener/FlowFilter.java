package cz.fb.manaus.reactor.betting.listener;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Runner;

import java.util.Set;
import java.util.function.BiPredicate;

public class FlowFilter {

    public static final FlowFilter ALLOW_ALL = new FlowFilter(Range.all(), Range.all(),
            (market, runner) -> true, Set.of());

    private final Range<Integer> indexRange;
    private final Range<Integer> winnerCountRange;
    private final BiPredicate<Market, Runner> runnerPredicate;
    private final Set<String> marketTypes;

    public FlowFilter(Range<Integer> indexRange, Range<Integer> winnerCountRange, BiPredicate<Market, Runner> runnerPredicate, Set<String> marketTypes) {
        this.indexRange = indexRange;
        this.winnerCountRange = winnerCountRange;
        this.runnerPredicate = runnerPredicate;
        this.marketTypes = marketTypes;
    }

    public Range<Integer> getIndexRange() {
        return indexRange;
    }

    public Range<Integer> getWinnerCountRange() {
        return winnerCountRange;
    }

    public BiPredicate<Market, Runner> getRunnerPredicate() {
        return runnerPredicate;
    }

    public Set<String> getMarketTypes() {
        return marketTypes;
    }
}
