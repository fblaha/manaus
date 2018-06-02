package cz.fb.manaus.manila.validator.update;

import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractDelayUpdateValidator;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ManilaBet
@Component
@Profile(ManausProfiles.DB_PROFILE)
public class DelayUpdateValidator extends AbstractDelayUpdateValidator {

    public DelayUpdateValidator() {
        super(Duration.ofMinutes(10));
    }

}
