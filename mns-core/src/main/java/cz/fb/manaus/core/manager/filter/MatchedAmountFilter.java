package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MatchedAmountFilter implements MarketFilter {

    @Override
    public boolean accept(Market market, Set<String> categoryBlacklist) {
        var matchedAmount = market.getMatchedAmount();
        return matchedAmount == null || !Price.amountEq(matchedAmount, 0d);
    }

}