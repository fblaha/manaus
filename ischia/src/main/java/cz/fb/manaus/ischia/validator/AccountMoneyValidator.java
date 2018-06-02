package cz.fb.manaus.ischia.validator;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.validator.common.AbstractAccountMoneyValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static cz.fb.manaus.spring.ManausProfiles.PRODUCTION_PROFILE;

@BackLoserBet
@LayLoserBet
@Component
@Profile(PRODUCTION_PROFILE)
public class AccountMoneyValidator extends AbstractAccountMoneyValidator {

    public AccountMoneyValidator() {
        super(25d);
    }
}
