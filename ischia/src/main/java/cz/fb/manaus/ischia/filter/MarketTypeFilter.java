package cz.fb.manaus.ischia.filter;

import com.google.common.base.Strings;
import cz.fb.manaus.core.manager.filter.MarketFilter;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MarketTypeFilter implements MarketFilter {

    private final Set<String> ALLOWED_TYPES = Set.of(
            "three_way",
            "match_odds",
            "rt_match_odds",
            "moneyline");

    @Override
    public boolean accept(Market market, Set<String> blacklist) {
        String type = Strings.nullToEmpty(market.getType()).toLowerCase();
        return ALLOWED_TYPES.contains(type);
    }
}
