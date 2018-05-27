package cz.fb.manaus.ischia.validator;

import com.google.common.collect.Range;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static cz.fb.manaus.reactor.betting.validator.ValidationResult.REJECT;

@LayLoserBet
@BackLoserBet
@Component
public class BestPriceRangeValidator implements Validator {

    public static final Range<Double> RANGE = Range.closed(1.3, 6d);

    @Override
    public ValidationResult validate(BetContext context) {
        Optional<Price> bestLay = context.getRunnerPrices().getHomogeneous(Side.LAY).getBestPrice();
        Optional<Price> bestBack = context.getRunnerPrices().getHomogeneous(Side.BACK).getBestPrice();
        if (bestBack.isPresent() && bestLay.isPresent()) {
            double backPrice = bestBack.get().getPrice();
            double layPrice = bestLay.get().getPrice();
            return ValidationResult.of(RANGE.containsAll(List.of(backPrice, layPrice)));
        } else {
            return REJECT;
        }
    }

}
