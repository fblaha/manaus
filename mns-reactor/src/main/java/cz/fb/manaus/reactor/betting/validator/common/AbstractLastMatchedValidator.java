package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

import static java.util.Objects.requireNonNull;

abstract public class AbstractLastMatchedValidator implements Validator {

    private final boolean passEqual;

    protected AbstractLastMatchedValidator(boolean passEqual) {
        this.passEqual = passEqual;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        var lastMatchedPrice = context.getRunnerPrices().getLastMatchedPrice();
        if (lastMatchedPrice == null) return ValidationResult.REJECT;
        if (Price.priceEq(context.getNewPrice().get().getPrice(), lastMatchedPrice)) {
            return ValidationResult.of(passEqual);
        }
        var side = requireNonNull(context.getSide());
        if (side == Side.LAY) {
            return ValidationResult.of(context.getNewPrice().get().getPrice() < lastMatchedPrice);
        } else {
            return ValidationResult.of(context.getNewPrice().get().getPrice() > lastMatchedPrice);
        }
    }

}
