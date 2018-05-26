package cz.fb.manaus.reactor.betting.validator.common.update;

import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static cz.fb.manaus.reactor.betting.validator.ValidationResult.of;

abstract public class AbstractDelayUpdateValidator implements Validator {

    private final Duration pausePeriod;
    @Autowired
    private BetActionDao actionDao;

    protected AbstractDelayUpdateValidator(Duration pausePeriod) {
        this.pausePeriod = pausePeriod;
    }

    @Override
    public boolean isUpdateOnly() {
        return true;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        var betId = context.getOldBet().get().getBetId();
        var actionDate = actionDao.getBetActionDate(betId).get();
        var untilNow = actionDate.toInstant().until(Instant.now(), ChronoUnit.MILLIS);
        return of(untilNow > pausePeriod.toMillis());
    }

}
