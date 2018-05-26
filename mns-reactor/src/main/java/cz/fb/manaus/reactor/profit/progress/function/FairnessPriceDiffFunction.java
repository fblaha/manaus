package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.OptionalDouble;

@Component
public class FairnessPriceDiffFunction implements ProgressFunction {

    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private PriceService priceService;

    @Override
    public OptionalDouble apply(SettledBet bet) {
        var marketPrices = bet.getBetAction().getMarketPrices();
        var fairness = calculator.getFairness(marketPrices);
        if (fairness.get(Side.LAY).isPresent() && fairness.get(Side.BACK).isPresent()) {
            var runnerPrices = marketPrices.getRunnerPrices(bet.getSelectionId());
            var layBest = runnerPrices.getHomogeneous(Side.LAY).getBestPrice();
            var backBest = runnerPrices.getHomogeneous(Side.BACK).getBestPrice();
            var layPrice = layBest.get().getPrice();
            var backPrice = backBest.get().getPrice();
            var fairnessLayFairPrice = priceService.getFairnessFairPrice(layPrice, fairness.get(Side.LAY).getAsDouble());
            var fairnessBackFairPrice = priceService.getFairnessFairPrice(backPrice, fairness.get(Side.BACK).getAsDouble());
            return OptionalDouble.of(Math.abs(fairnessBackFairPrice - fairnessLayFairPrice));
        } else {
            return OptionalDouble.empty();
        }
    }

}
