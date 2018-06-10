package cz.fb.manaus.core.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Table;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Comparator.comparing;

public class MarketSnapshot {

    private static final Logger log = Logger.getLogger(MarketSnapshot.class.getSimpleName());

    private final MarketPrices marketPrices;
    private final List<Bet> currentBets;
    private final Table<Side, Long, Bet> coverage;
    private final Optional<Map<Long, TradedVolume>> tradedVolume;

    public MarketSnapshot(MarketPrices marketPrices, List<Bet> currentBets,
                          Table<Side, Long, Bet> coverage,
                          Optional<Map<Long, TradedVolume>> tradedVolume) {
        this.marketPrices = marketPrices;
        this.currentBets = currentBets;
        this.coverage = coverage;
        this.tradedVolume = tradedVolume;
    }

    static Table<Side, Long, Bet> getMarketCoverage(List<Bet> bets) {
        var sortedBets = Ordering.from(comparing(Bet::getPlacedDate))
                .immutableSortedCopy(bets);

        var result = HashBasedTable.<Side, Long, Bet>create();
        for (var bet : sortedBets) {
            var side = bet.getRequestedPrice().getSide();
            var predecessor = result.get(side, bet.getSelectionId());
            if (predecessor != null) {
                log.log(Level.WARNING, "Suspicious relationship between predecessor ''{0}'' and successor ''{1}''",
                        new Object[]{predecessor, bet});

            }
            result.put(side, bet.getSelectionId(), bet);
        }
        return result;
    }

    public static MarketSnapshot from(MarketPrices marketPrices, List<Bet> currentBets,
                                      Optional<Map<Long, TradedVolume>> tradedVolume) {
        var coverage = getMarketCoverage(currentBets);
        return new MarketSnapshot(marketPrices, currentBets, coverage, tradedVolume);
    }

    public List<Bet> getCurrentBets() {
        return currentBets;
    }

    public Optional<Map<Long, TradedVolume>> getTradedVolume() {
        return tradedVolume;
    }

    public MarketPrices getMarketPrices() {
        return marketPrices;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("marketPrices", marketPrices)
                .add("currentBets", currentBets)
                .add("tradedVolume", tradedVolume)
                .toString();
    }

    public Table<Side, Long, Bet> getCoverage() {
        return coverage;
    }

}
