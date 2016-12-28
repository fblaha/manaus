package cz.fb.manaus.reactor.betting.validator;

import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.NameAware;

public interface Validator extends NameAware {

    default boolean isDowngradeAccepting() {
        return true;
    }

    default boolean isUpdateOnly() {
        return false;
    }

    default boolean isPriceRequired() {
        return true;
    }

    ValidationResult validate(BetContext context);

}
