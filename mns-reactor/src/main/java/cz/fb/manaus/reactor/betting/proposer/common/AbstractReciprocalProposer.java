package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;


abstract public class AbstractReciprocalProposer implements PriceProposer {

    private final Side side;
    private final Function<BetContext, Double> downgradeStrategy;
    @Autowired
    private PriceService priceService;

    public AbstractReciprocalProposer(Side side, Function<BetContext, Double> downgradeStrategy) {
        this.side = side;
        this.downgradeStrategy = downgradeStrategy;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        OptionalDouble reciprocal = context.getMarketPrices().getReciprocal(side);
        return ValidationResult.of(reciprocal.isPresent() && getProposedPrice(context).isPresent());
    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        double reciprocal = context.getMarketPrices().getReciprocal(side).getAsDouble();
        Optional<Price> bestPrice = context.getRunnerPrices().getHomogeneous(side).getBestPrice();
        if (bestPrice.isPresent()) {
            double fairPrice = priceService.getReciprocalFairPrice(bestPrice.get().getPrice(), reciprocal);
            double downgradeFraction = downgradeStrategy.apply(context);
            double price = priceService.downgrade(fairPrice, downgradeFraction, context.getSide());
            return OptionalDouble.of(price);
        }
        return OptionalDouble.empty();
    }
}
