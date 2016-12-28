package cz.fb.manaus.core.category;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BetCoverage {

    public static final BetCoverage EMPTY = new BetCoverage(ImmutableTable.of());
    private final Table<String, Long, List<SettledBet>> coverage;


    private BetCoverage(Table<String, Long, List<SettledBet>> coverage) {
        this.coverage = coverage;
    }

    private static Table<String, Long, List<SettledBet>> buildCoverage(Iterable<SettledBet> settledBets) {
        ImmutableTable.Builder<String, Long, List<SettledBet>> builder = ImmutableTable.builder();
        Set<ImmutablePair<String, Long>> coverKeys = FluentIterable.from(settledBets)
                .transform(BetCoverage::getCoverageKey)
                .toSet();
        coverKeys.forEach(key -> builder.put(key.getLeft(), key.getRight(), new LinkedList<>()));
        Table<String, Long, List<SettledBet>> coverage = builder.build();
        settledBets.forEach(bet -> coverage.get(bet.getBetAction().getMarket().getId(), bet.getSelectionId()).add(bet));
        return coverage;
    }

    private static ImmutablePair<String, Long> getCoverageKey(SettledBet bet) {
        return new ImmutablePair<>(bet.getBetAction().getMarket().getId(), bet.getSelectionId());
    }

    public static BetCoverage from(Iterable<SettledBet> bets) {
        return new BetCoverage(buildCoverage(bets));
    }

    public List<SettledBet> getBets(String marketId, long selectionId, Side side) {
        FluentIterable<SettledBet> bets = FluentIterable.from(coverage.get(marketId, selectionId));
        if (side != null) {
            bets = bets.filter(bet -> bet.getPrice().getSide() == side);
        }
        return bets.toList();
    }

    public Set<Side> getSides(String marketId, long selectionId) {
        return coverage.get(marketId, selectionId).stream()
                .map(SettledBet::getPrice)
                .map(Price::getSide)
                .collect(Collectors.toSet());
    }

    public boolean isCovered(String marketId, long selectionId) {
        Set<Side> sides = getSides(marketId, selectionId);
        return sides.size() == 2;
    }

    public double getAmount(String marketId, long selectionId, Side side) {
        return coverage.get(marketId, selectionId).stream()
                .map(SettledBet::getPrice)
                .filter(price -> price.getSide() == side)
                .mapToDouble(Price::getAmount)
                .sum();
    }

    public double getPrice(String marketId, long selectionId, Side side) {
        return coverage.get(marketId, selectionId).stream()
                .map(SettledBet::getPrice)
                .filter(price -> price.getSide() == side)
                .mapToDouble(Price::getPrice)
                .average().getAsDouble();
    }

}