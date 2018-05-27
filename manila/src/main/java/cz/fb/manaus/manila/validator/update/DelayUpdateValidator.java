package cz.fb.manaus.manila.validator.update;

import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractDelayUpdateValidator;
import cz.fb.manaus.spring.DatabaseComponent;

import java.time.Duration;

@ManilaBet
@DatabaseComponent
public class DelayUpdateValidator extends AbstractDelayUpdateValidator {

    public DelayUpdateValidator() {
        super(Duration.ofMinutes(10));
    }

}
