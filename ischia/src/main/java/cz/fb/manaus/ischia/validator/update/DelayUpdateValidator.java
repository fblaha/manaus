package cz.fb.manaus.ischia.validator.update;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractDelayUpdateValidator;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;

@BackLoserBet
@LayLoserBet
@Component
@Profile(ManausProfiles.DB_PROFILE)
public class DelayUpdateValidator extends AbstractDelayUpdateValidator {

    public DelayUpdateValidator() {
        super(Duration.ofMinutes(10));
    }

}
