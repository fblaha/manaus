package cz.fb.manaus.reactor.betting.validator.common.update;

import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.apache.commons.math3.util.Precision;

public abstract class AbstractTooCloseUpdateEpsilonValidator implements Validator {
    private final double epsilon;

    protected AbstractTooCloseUpdateEpsilonValidator(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        double oldOne = context.getOldBet().get().getRequestedPrice().getPrice();
        double newOne = context.getNewPrice().get().getPrice();
        double epsilon = (oldOne - 1) * this.epsilon;
        return ValidationResult.of(!Precision.equals(newOne, oldOne, epsilon));
    }

    @Override
    public boolean isUpdateOnly() {
        return true;
    }

}
