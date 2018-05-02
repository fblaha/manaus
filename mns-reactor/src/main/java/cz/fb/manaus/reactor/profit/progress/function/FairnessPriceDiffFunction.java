package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.price.Fairness;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.OptionalDouble;

@Component
public class FairnessPriceDiffFunction implements ProgressFunction {

    @Autowired
    private FairnessPolynomialCalculator calculator;
    @Autowired
    private PriceService priceService;

    @Override
    public OptionalDouble apply(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        Fairness fairness = calculator.getFairness(marketPrices);
        if (fairness.get(Side.LAY).isPresent() && fairness.get(Side.BACK).isPresent()) {
            RunnerPrices runnerPrices = marketPrices.getRunnerPrices(bet.getSelectionId());
            Optional<Price> layBest = runnerPrices.getHomogeneous(Side.LAY).getBestPrice();
            Optional<Price> backBest = runnerPrices.getHomogeneous(Side.BACK).getBestPrice();
            double layPrice = layBest.get().getPrice();
            double backPrice = backBest.get().getPrice();
            double fairnessLayFairPrice = priceService.getFairnessFairPrice(layPrice, fairness.get(Side.LAY).getAsDouble());
            double fairnessBackFairPrice = priceService.getFairnessFairPrice(backPrice, fairness.get(Side.BACK).getAsDouble());
            return OptionalDouble.of(Math.abs(fairnessBackFairPrice - fairnessLayFairPrice));
        } else {
            return OptionalDouble.empty();
        }
    }

}
