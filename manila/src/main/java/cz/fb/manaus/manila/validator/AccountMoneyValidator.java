package cz.fb.manaus.manila.validator;

import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.validator.common.AbstractAccountMoneyValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static cz.fb.manaus.spring.CoreLocalConfiguration.PRODUCTION_PROFILE;

@ManilaBet
@Component
@Profile(PRODUCTION_PROFILE)
public class AccountMoneyValidator extends AbstractAccountMoneyValidator {

    public AccountMoneyValidator() {
        super(25d);
    }
}
