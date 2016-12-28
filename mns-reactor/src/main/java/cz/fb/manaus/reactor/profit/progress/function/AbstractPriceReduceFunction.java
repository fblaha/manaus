package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;

import java.util.List;
import java.util.OptionalDouble;
import java.util.function.DoubleBinaryOperator;

public abstract class AbstractPriceReduceFunction implements ProgressFunction {

    private final Side side;
    private final DoubleBinaryOperator operator;

    protected AbstractPriceReduceFunction(Side side, DoubleBinaryOperator operator) {
        this.side = side;
        this.operator = operator;
    }

    @Override
    public OptionalDouble function(SettledBet bet) {
        MarketPrices marketPrices = bet.getBetAction().getMarketPrices().getHomogeneous(side);
        List<OptionalDouble> bestPrices = marketPrices.getBestPrices(side);
        if (bestPrices.stream().allMatch(OptionalDouble::isPresent)) {
            return bestPrices.stream()
                    .filter(OptionalDouble::isPresent)
                    .mapToDouble(OptionalDouble::getAsDouble)
                    .reduce(operator);
        } else {
            return OptionalDouble.empty();
        }
    }
}
