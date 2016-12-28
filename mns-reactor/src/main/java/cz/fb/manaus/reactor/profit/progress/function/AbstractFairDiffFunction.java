package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.OptionalDouble;

public abstract class AbstractFairDiffFunction implements ProgressFunction {

    private final Side side;

    @Autowired
    private PriceService priceService;
    @Autowired
    private FairnessPolynomialCalculator calculator;

    protected AbstractFairDiffFunction(Side side) {
        this.side = side;
    }

    @Override
    public OptionalDouble function(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        OptionalDouble reciprocal = marketPrices.getReciprocal(side);
        List<OptionalDouble> bestPrices = marketPrices.getBestPrices(side);
        OptionalDouble fairness = calculator.getFairness(marketPrices.getWinnerCount(), bestPrices);
        if (fairness.isPresent() && reciprocal.isPresent()) {
            RunnerPrices prices = marketPrices.getRunnerPrices(bet.getSelectionId());
            double bestPrice = prices.getHomogeneous(side).getBestPrice().get().getPrice();
            double fairnessFairPrice = priceService.getFairnessFairPrice(bestPrice, fairness.getAsDouble());
            double reciprocalFairPrice = priceService.getReciprocalFairPrice(bestPrice, reciprocal.getAsDouble());
            return OptionalDouble.of(reciprocalFairPrice - fairnessFairPrice);

        }
        return OptionalDouble.empty();
    }

}
