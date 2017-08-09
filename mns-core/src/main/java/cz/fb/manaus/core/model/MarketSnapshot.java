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

    public MarketSnapshot(MarketPrices marketPrices, List<Bet> currentBets, Optional<Map<Long, TradedVolume>> tradedVolume) {
        this.marketPrices = marketPrices;
        this.currentBets = currentBets;
        this.tradedVolume = tradedVolume;
        this.coverage = getMarketCoverage(currentBets);
    }

    static Table<Side, Long, Bet> getMarketCoverage(List<Bet> bets) {
        try {
            List<Bet> sortedBets = Ordering.from(comparing(Bet::getPlacedDate))
                    .immutableSortedCopy(bets);

            Table<Side, Long, Bet> result = HashBasedTable.create();
            for (Bet bet : sortedBets) {
                Side side = bet.getRequestedPrice().getSide();
                Bet predecessor = result.get(side, bet.getSelectionId());
                if (predecessor != null) {
                    bet.setPredecessor(predecessor);
                    log.log(Level.WARNING, "Suspicious relationship between predecessor ''{0}'' and successor ''{1}''",
                            new Object[]{predecessor, bet});

                }
                result.put(side, bet.getSelectionId(), bet);
            }
            return result;
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "Illegal bets ''{0}''", bets);
            throw e;
        }
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
