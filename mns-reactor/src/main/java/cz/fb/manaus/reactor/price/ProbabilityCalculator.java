package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProbabilityCalculator {

    @Autowired
    private PriceService priceService;

    public Map<Long, Double> fromFairness(double fairness, Side side, MarketPrices prices) {
        var sidePrices = prices.getHomogeneous(side);
        var runnerPrices = sidePrices.getRunnerPrices();

        var result = new HashMap<Long, Double>();
        for (var runnerPrice : runnerPrices) {
            var bestPrice = runnerPrice.getBestPrice();
            var unfairPrice = bestPrice.get().getPrice();
            var fairPrice = priceService.getFairnessFairPrice(unfairPrice, fairness);
            result.put(runnerPrice.getSelectionId(), 1 / fairPrice);
        }
        return result;
    }

}
