package cz.fb.manaus.reactor.betting.proposer;

import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.NameAware;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;

import java.util.OptionalDouble;

public interface PriceProposer extends Validator, NameAware {

    OptionalDouble getProposedPrice(BetContext context);

    @Override
    default boolean isPriceRequired() {
        return false;
    }

    @Override
    default boolean isDowngradeAccepting() {
        return false;
    }

    @Override
    default ValidationResult validate(BetContext context) {
        return ValidationResult.ACCEPT;
    }

    default boolean isMandatory() {
        return true;
    }
}
