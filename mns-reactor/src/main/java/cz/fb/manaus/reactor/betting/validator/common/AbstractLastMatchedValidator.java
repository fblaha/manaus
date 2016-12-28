package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

import static com.google.common.base.Preconditions.checkNotNull;

abstract public class AbstractLastMatchedValidator implements Validator {

    private final boolean passEqual;

    protected AbstractLastMatchedValidator(boolean passEqual) {
        this.passEqual = passEqual;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        Double lastMatchedPrice = context.getRunnerPrices().getLastMatchedPrice();
        if (lastMatchedPrice == null) return ValidationResult.REJECT;
        if (Price.priceEq(context.getNewPrice().get().getPrice(), lastMatchedPrice)) {
            return ValidationResult.of(passEqual);
        }
        Side side = checkNotNull(context.getSide());
        if (side == Side.LAY) {
            return ValidationResult.of(context.getNewPrice().get().getPrice() < lastMatchedPrice);
        } else {
            return ValidationResult.of(context.getNewPrice().get().getPrice() > lastMatchedPrice);
        }
    }

}
