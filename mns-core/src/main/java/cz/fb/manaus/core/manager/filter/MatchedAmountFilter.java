package cz.fb.manaus.core.manager.filter;

import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import org.springframework.stereotype.Component;

@Component
public class MatchedAmountFilter implements MarketFilter {

    @Override
    public boolean test(Market market) {
        Double matchedAmount = market.getMatchedAmount();
        return matchedAmount == null || !Price.amountEq(matchedAmount, 0d);
    }

}