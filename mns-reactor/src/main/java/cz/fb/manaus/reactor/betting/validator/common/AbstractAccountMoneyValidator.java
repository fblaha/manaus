package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

public class AbstractAccountMoneyValidator implements Validator {

    private final double minimalAvailable;

    public AbstractAccountMoneyValidator(double minimalAvailable) {
        this.minimalAvailable = minimalAvailable;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        return context.getMarketSnapshot().getAccountMoney()
                .map(am -> ValidationResult.of(am.getAvailable() > minimalAvailable))
                .orElse(ValidationResult.ACCEPT);
    }
}
