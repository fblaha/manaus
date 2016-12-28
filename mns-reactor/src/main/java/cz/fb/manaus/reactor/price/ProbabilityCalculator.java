package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ProbabilityCalculator {

    @Autowired
    private PriceService priceService;

    public Map<Long, Double> fromFairness(double fairness, Side side, MarketPrices prices) {
        MarketPrices sidePrices = prices.getHomogeneous(side);
        Collection<RunnerPrices> runnerPrices = sidePrices.getRunnerPrices();

        Map<Long, Double> result = new HashMap<>();
        for (RunnerPrices runnerPrice : runnerPrices) {
            Optional<Price> bestPrice = runnerPrice.getBestPrice();
            double unfairPrice = bestPrice.get().getPrice();
            double fairPrice = priceService.getFairnessFairPrice(unfairPrice, fairness);
            result.put(runnerPrice.getSelectionId(), 1 / fairPrice);
        }
        return result;
    }

}
