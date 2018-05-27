package cz.fb.manaus.manila.validator;

import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@ManilaBet
@Component
public class CloseStartValidator implements Validator {

    @Override
    public ValidationResult validate(BetContext context) {
        var openDate = context.getMarketPrices().getMarket().getEvent().getOpenDate();
        var seconds = Instant.now().until(openDate.toInstant(), ChronoUnit.SECONDS);
        return ValidationResult.of(seconds > 30);
    }

}
