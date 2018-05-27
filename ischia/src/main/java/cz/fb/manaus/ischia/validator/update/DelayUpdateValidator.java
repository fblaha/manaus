package cz.fb.manaus.ischia.validator.update;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractDelayUpdateValidator;
import cz.fb.manaus.spring.DatabaseComponent;

import java.time.Duration;

@BackLoserBet
@LayLoserBet
@DatabaseComponent
public class DelayUpdateValidator extends AbstractDelayUpdateValidator {

    public DelayUpdateValidator() {
        super(Duration.ofMinutes(10));
    }

}
