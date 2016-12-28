package cz.fb.manaus.reactor.categorizer;


import com.google.common.collect.ImmutableMap;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
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
        String marketId = settledBet.getBetAction().getMarket().getId();
        long selectionId = settledBet.getSelectionId();
        Side side = settledBet.getPrice().getSide();
        Side counterSide = side.getOpposite();
        List<SettledBet> bets = coverage.getBets(marketId, selectionId, counterSide);
        OptionalDouble avgCounter = bets.stream()
                .map(SettledBet::getPrice)
                .mapToDouble(Price::getPrice)
                .average();
        if (avgCounter.isPresent()) {
            double counterPrice = avgCounter.getAsDouble();
            double price = settledBet.getPrice().getPrice();
            ImmutableMap<Side, Double> prices = ImmutableMap.of(side, price, counterSide, counterPrice);
            if (Price.priceEq(counterPrice, price)) {
                return Collections.singleton(PREFIX + "zero");
            } else if (prices.get(Side.BACK) > prices.get(Side.LAY)) {
                return Collections.singleton(PREFIX + "profit");
            } else if (prices.get(Side.BACK) < prices.get(Side.LAY)) {
                return Collections.singleton(PREFIX + "loss");
            } else {
                throw new IllegalStateException();
            }
        } else {
            return Collections.singleton(PREFIX + "none");
        }
    }

}
