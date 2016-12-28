package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.core.money.AccountMoneyRegistry;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractAccountMoneyValidator implements Validator {

    private final double minimalAvailable;
    @Autowired
    private AccountMoneyRegistry registry;

    public AbstractAccountMoneyValidator(double minimalAvailable) {
        this.minimalAvailable = minimalAvailable;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        if (context.isUpdate()) {
            return ValidationResult.ACCEPT;
        } else {
            return ValidationResult.of(registry.getMoney().get().getAvailable() > minimalAvailable);
        }
    }
}
