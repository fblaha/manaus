package cz.fb.manaus.reactor.categorizer;


import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class CounterBetCategorizer implements SettledBetCategorizer {

    public static final String PREFIX = "counter_";

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var marketId = settledBet.getBetAction().getMarket().getId();
        var selectionId = settledBet.getSelectionId();
        var side = settledBet.getPrice().getSide();
        var counterSide = side.getOpposite();
        var bets = coverage.getBets(marketId, selectionId, counterSide);
        var avgCounter = bets.stream()
                .map(SettledBet::getPrice)
                .mapToDouble(Price::getPrice)
                .average();
        if (avgCounter.isPresent()) {
            var counterPrice = avgCounter.getAsDouble();
            var price = settledBet.getPrice().getPrice();
            var prices = Map.of(side, price, counterSide, counterPrice);
            if (Price.priceEq(counterPrice, price)) {
                return Set.of(PREFIX + "zero");
            } else if (prices.get(Side.BACK) > prices.get(Side.LAY)) {
                return Set.of(PREFIX + "profit");
            } else if (prices.get(Side.BACK) < prices.get(Side.LAY)) {
                return Set.of(PREFIX + "loss");
            } else {
                throw new IllegalStateException();
            }
        } else {
            return Set.of(PREFIX + "none");
        }
    }

}
