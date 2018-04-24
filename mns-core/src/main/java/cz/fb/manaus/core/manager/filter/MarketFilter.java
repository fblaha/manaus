package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;

import java.util.Set;

public interface MarketFilter {

    boolean accept(Market market, Set<String> categoryBlacklist);

    default boolean isStrict() {
        return false;
    }
}
