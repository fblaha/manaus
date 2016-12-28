package cz.fb.manaus.core.manager;

import cz.fb.manaus.core.manager.filter.MarketFilter;
import cz.fb.manaus.core.model.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MarketFilterService {
    @Autowired
    private List<MarketFilter> marketFilters;

    public boolean accept(Market input) {
        return marketFilters.stream().allMatch(filter -> filter.test(input));
    }

}
