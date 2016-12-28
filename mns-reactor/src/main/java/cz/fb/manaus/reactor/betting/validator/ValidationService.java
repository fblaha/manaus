package cz.fb.manaus.reactor.betting.validator;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;

@Service
public class ValidationService {

    @Autowired
    private PriceService priceService;
    @Autowired
    private ValidationStatisticsRecorder recorder;

    Optional<ValidationResult> handleDowngrade(Optional<Price> newOne, Optional<Bet> oldOne, Validator validator) {
        if (oldOne.isPresent() && newOne.isPresent()) {
            Price oldPrice = oldOne.get().getRequestedPrice();
            checkState(newOne.get().getSide() == checkNotNull(oldPrice.getSide()), validator.getClass());
            if (priceService.isDowngrade(newOne.get().getPrice(), oldPrice.getPrice(),
                    newOne.get().getSide()) && validator.isDowngradeAccepting()) {
                return Optional.of(ValidationResult.ACCEPT);
            }
        }
        return Optional.empty();
    }

    ValidationResult reduce(List<ValidationResult> results) {
        checkState(!results.isEmpty());
        return ValidationResult.of(results.stream().allMatch(ValidationResult::isSuccess));
    }

    public ValidationResult validate(BetContext context, Iterable<Validator> validators) {
        List<Validator> filteredValidators = from(validators).filter(createPredicate(context)).toList();
        Preconditions.checkState(!filteredValidators.isEmpty());

        Optional<Price> newPrice = context.getNewPrice();
        List<ValidationResult> collected = new LinkedList<>();
        for (Validator validator : filteredValidators) {
            if (validator.isUpdateOnly()) {
                Preconditions.checkState(context.getOldBet().isPresent());
            }
            ValidationResult validationResult = handleDowngrade(newPrice, context.getOldBet(), validator)
                    .orElse(checkNotNull(validator.validate(context)));
            recorder.record(validationResult, context.getSide(), validator);
            collected.add(validationResult);
        }
        return checkNotNull(reduce(collected));
    }

    private Predicate<Validator> createPredicate(BetContext context) {
        List<Predicate<Validator>> predicates = new LinkedList<>();
        if (!context.getOldBet().isPresent()) {
            predicates.add(not(Validator::isUpdateOnly));
        }
        if (context.getNewPrice().isPresent()) {
            predicates.add(Validator::isPriceRequired);
        } else {
            predicates.add(not(Validator::isPriceRequired));
        }
        return Predicates.and(predicates);
    }

}
