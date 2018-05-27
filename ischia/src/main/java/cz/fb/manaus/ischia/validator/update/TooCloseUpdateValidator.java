package cz.fb.manaus.ischia.validator.update;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("betfair")
final public class TooCloseUpdateValidator extends AbstractTooCloseUpdateValidator {

    public TooCloseUpdateValidator() {
        super(Set.of(-1, 1));
    }

    @Override
    public ValidationResult validate(BetContext context) {
        if (context.isCounterHalfMatched()) {
            return ValidationResult.ACCEPT;
        } else {
            return super.validate(context);
        }
    }
}
