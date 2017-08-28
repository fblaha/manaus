package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.money.AccountMoneyRepository;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AbstractAccountMoneyValidator implements Validator {

    private final double minimalAvailable;
    @Autowired
    private Optional<AccountMoneyRepository> repository;

    public AbstractAccountMoneyValidator(double minimalAvailable) {
        this.minimalAvailable = minimalAvailable;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        if (!context.isUpdate() && repository.isPresent()) {
            Optional<AccountMoney> accountMoney = repository.flatMap(r -> r.getAccountMoney());
            if (accountMoney.isPresent()) {
                return ValidationResult.of(accountMoney.get().getAvailable() > minimalAvailable);
            }
        }
        return ValidationResult.ACCEPT;
    }
}
