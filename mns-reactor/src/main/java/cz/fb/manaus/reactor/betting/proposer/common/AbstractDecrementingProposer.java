package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalDouble;

public abstract class AbstractDecrementingProposer implements PriceProposer {
    private final int decrementSteps;
    @Autowired
    private RoundingService roundingService;

    protected AbstractDecrementingProposer(int decrementSteps) {
        this.decrementSteps = decrementSteps;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        var proposedPrice = getProposedPrice(context);
        return proposedPrice.isPresent() ? ValidationResult.ACCEPT : ValidationResult.REJECT;

    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        var originalPrice = roundingService.roundBet(getOriginalPrice(context));
        if (originalPrice.isPresent()) {
            return roundingService.decrement(originalPrice.getAsDouble(), decrementSteps);
        } else {
            return OptionalDouble.empty();
        }
    }

    protected abstract double getOriginalPrice(BetContext betContext);
}
