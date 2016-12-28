package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalDouble;
import java.util.function.Function;

public abstract class AbstractFairnessProposer implements PriceProposer {

    private final Side side;
    private final Function<BetContext, Double> downgradeStrategy;
    @Autowired
    private PriceService priceService;

    public AbstractFairnessProposer(Side side, Function<BetContext, Double> downgradeStrategy) {
        this.side = side;
        this.downgradeStrategy = downgradeStrategy;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        return ValidationResult.of(context.getFairness().get(side).isPresent());
    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        OptionalDouble fairness = context.getFairness().get(side);
        Price bestPrice = context.getRunnerPrices().getHomogeneous(side).getBestPrice().get();
        double fairPrice = priceService.getFairnessFairPrice(bestPrice.getPrice(), fairness.getAsDouble());
        double downgradeFraction = downgradeStrategy.apply(context);
        return OptionalDouble.of(priceService.downgrade(fairPrice, downgradeFraction, context.getSide()));
    }
}
