package cz.fb.manaus.core.manager;

import cz.fb.manaus.core.manager.filter.MarketFilter;
import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class MarketFilterService {
    @Autowired
    private List<MarketFilter> marketFilters;

    public boolean accept(Market market, boolean hasBets, Set<String> categoryBlacklist) {
        var filters = marketFilters.stream();
        if (hasBets) {
            filters = filters.filter(MarketFilter::isStrict);
        }
        return filters.allMatch(filter -> filter.accept(market, categoryBlacklist));
    }
}
