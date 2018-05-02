package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.price.FairnessPolynomialCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class LastMatchedFairnessFunction implements ProgressFunction {

    @Autowired
    private FairnessPolynomialCalculator calculator;

    @Override
    public OptionalDouble apply(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices();
        List<OptionalDouble> lastMatched = marketPrices.getRunnerPrices().stream()
                .map(this::getLastMatched)
                .collect(Collectors.toList());
        return calculator.getFairness(marketPrices.getWinnerCount(), lastMatched);
    }

    private OptionalDouble getLastMatched(RunnerPrices runnerPrices) {
        Double lastMatchedPrice = runnerPrices.getLastMatchedPrice();
        return lastMatchedPrice == null ? OptionalDouble.empty() : OptionalDouble.of(lastMatchedPrice);
    }

}
