package cz.fb.manaus.reactor.betting.validator.common;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

import java.util.OptionalDouble;

public abstract class AbstractFairnessValidator implements Validator {
    protected final Range<Double> validRange;
    private final Side side;

    public AbstractFairnessValidator(Side side, Range<Double> range) {
        this.side = side;
        validRange = range;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        OptionalDouble fairness = context.getFairness().get(side);
        return ValidationResult.of(fairness.isPresent() && validRange.contains(fairness.getAsDouble()));
    }
}
