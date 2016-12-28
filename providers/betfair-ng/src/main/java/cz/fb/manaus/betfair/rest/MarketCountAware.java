package cz.fb.manaus.betfair.rest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public interface MarketCountAware {
    int MARKET_LIMIT = 100;

    static <T extends MarketCountAware> List<List<T>> split(Iterable<T> countAwareList) {
        int sum = 0;
        List<List<T>> result = new LinkedList<>();
        List<T> current = new LinkedList<>();
        for (T countAware : from(countAwareList).filter(cntAware -> cntAware.getMarketCount() <= MARKET_LIMIT)) {
            if (sum + countAware.getMarketCount() > MARKET_LIMIT) {
                result.add(current);
                current = new LinkedList<>();
                current.add(countAware);
                sum = countAware.getMarketCount();
            } else {
                sum += countAware.getMarketCount();
                current.add(countAware);
            }
        }
        if (!current.isEmpty()) result.add(current);
        for (T countAware : from(countAwareList).filter(cntAware -> cntAware.getMarketCount() > MARKET_LIMIT)) {
            result.add(Collections.singletonList(countAware));
        }
        return result;
    }

    default boolean isOverLimit() {
        return getMarketCount() > MARKET_LIMIT;
    }

    int getMarketCount();

}
