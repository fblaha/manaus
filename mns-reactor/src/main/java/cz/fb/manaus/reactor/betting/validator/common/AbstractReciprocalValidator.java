package cz.fb.manaus.reactor.betting.validator.common;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

public abstract class AbstractReciprocalValidator implements Validator {
    protected final Range<Double> validRange;
    private final Side type;

    public AbstractReciprocalValidator(Side type, Range<Double> range) {
        this.type = type;
        validRange = range;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        var reciprocal = context.getMarketPrices().getReciprocal(type).getAsDouble();
        return ValidationResult.of(validRange.contains(reciprocal));
    }
}
