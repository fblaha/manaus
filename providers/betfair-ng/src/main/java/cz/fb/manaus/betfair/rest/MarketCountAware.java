package cz.fb.manaus.betfair.rest;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public interface MarketCountAware {
    int MARKET_LIMIT = 100;

    static <T extends MarketCountAware> List<List<T>> split(List<T> countAwareList) {
        int sum = 0;
        List<List<T>> result = new LinkedList<>();
        List<T> current = new LinkedList<>();
        List<T> smallMarkets = countAwareList.stream().
                filter(cntAware -> cntAware.getMarketCount() <= MARKET_LIMIT).
                collect(Collectors.toList());
        for (T countAware : smallMarkets) {
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
        countAwareList.stream()
                .filter(MarketCountAware::isOverLimit)
                .forEach(countAware -> result.add(Collections.singletonList(countAware)));
        return result;
    }

    default boolean isOverLimit() {
        return getMarketCount() > MARKET_LIMIT;
    }

    int getMarketCount();

}
