package cz.fb.manaus.reactor.betting.validator.common.update;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

public abstract class AbstractTooCloseUpdateEpsilonValidator implements Validator {
    private final double epsilon;

    protected AbstractTooCloseUpdateEpsilonValidator(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        var oldOne = context.getOldBet().get().getRequestedPrice().getPrice();
        var newOne = context.getNewPrice().get().getPrice();
        var epsilon = (oldOne - 1) * this.epsilon;
        var closeRange = Range.closed(oldOne - epsilon, oldOne + epsilon);
        return ValidationResult.of(!closeRange.contains(newOne));
    }

    @Override
    public boolean isUpdateOnly() {
        return true;
    }

}
