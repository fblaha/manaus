package cz.fb.manaus.manila.validator;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT;

@ManilaBet
@Component
public class BestPriceRangeValidator implements Validator {

    public static final Range<Double> RANGE = Range.closed(1.2, 2.5);

    @Override
    public ValidationResult validate(BetContext context) {
        Optional<Price> bestBack = context.getRunnerPrices().getHomogeneous(Side.BACK).getBestPrice();
        return bestBack
                .map(price -> ValidationResult.of(RANGE.contains(price.getPrice())))
                .orElse(REJECT);
    }

}
