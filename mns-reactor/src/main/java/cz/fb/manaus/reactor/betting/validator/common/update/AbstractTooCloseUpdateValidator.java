package cz.fb.manaus.reactor.betting.validator.common.update;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.OptionalDouble;
import java.util.Set;

abstract public class AbstractTooCloseUpdateValidator implements Validator {
    private final Set<Integer> closeSteps;
    @Autowired
    private RoundingService roundingService;
    @Autowired
    private PriceService priceService;

    public AbstractTooCloseUpdateValidator(Set<Integer> closeSteps) {
        this.closeSteps = closeSteps;
    }

    @Override
    public boolean isUpdateOnly() {
        return true;
    }

    @Override
    public ValidationResult validate(BetContext context) {
        double oldOne = context.getOldBet().get().getRequestedPrice().getPrice();
        double newOne = context.getNewPrice().get().getPrice();
        if (Price.priceEq(newOne, oldOne)) return ValidationResult.REJECT;
        for (Integer step : closeSteps) {
            Preconditions.checkArgument(step != 0);
            OptionalDouble closePrice;
            if (step > 0) {
                closePrice = roundingService.increment(oldOne, step);
            } else {
                closePrice = roundingService.decrement(oldOne, -step);
            }
            if (closePrice.isPresent() && Price.priceEq(newOne, closePrice.getAsDouble()))
                return ValidationResult.REJECT;
        }
        return ValidationResult.ACCEPT;
    }

}
