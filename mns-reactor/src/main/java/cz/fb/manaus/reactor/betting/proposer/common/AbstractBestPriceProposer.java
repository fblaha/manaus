package cz.fb.manaus.reactor.betting.proposer.common;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalDouble;

import static java.util.Objects.requireNonNull;

public abstract class AbstractBestPriceProposer implements PriceProposer {

    @Autowired
    private RoundingService roundingService;

    public AbstractBestPriceProposer() {

    }

    @Override
    public ValidationResult validate(BetContext context) {
        var runnerPrices = context.getRunnerPrices();
        var homogeneous = runnerPrices.getHomogeneous(context.getSide().getOpposite());
        var bestPrice = homogeneous.getBestPrice();
        if (bestPrice.isPresent()) {
            return PriceProposer.super.validate(context);
        } else {
            return ValidationResult.REJECT;
        }
    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        var side = requireNonNull(context.getSide());
        var bestPrice = context.getRunnerPrices().getHomogeneous(side.getOpposite()).getBestPrice().get().getPrice();
        var step = getStep();
        Preconditions.checkState(step >= 0);
        if (step == 0) {
            return OptionalDouble.of(bestPrice);
        } else {
            if (side == Side.LAY) {
                return roundingService.increment(bestPrice, step);
            } else {
                return roundingService.decrement(bestPrice, step);
            }
        }
    }

    protected int getStep() {
        return 1;
    }
}
