package cz.fb.manaus.reactor.betting.validator;

import com.google.common.base.Preconditions;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

@Service
public class ValidationService {

    @Autowired
    private PriceService priceService;
    @Autowired
    private ValidationMetricsCollector recorder;

    Optional<ValidationResult> handleDowngrade(Optional<Price> newOne, Optional<Bet> oldOne, Validator validator) {
        if (oldOne.isPresent() && newOne.isPresent()) {
            Price oldPrice = oldOne.get().getRequestedPrice();
            checkState(newOne.get().getSide() == requireNonNull(oldPrice.getSide()), validator.getClass());
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

    public ValidationResult validate(BetContext context, List<Validator> validators) {
        var filteredValidators = validators.stream()
                .filter(createPredicate(context)).collect(Collectors.toList());
        Preconditions.checkState(!filteredValidators.isEmpty());

        var newPrice = context.getNewPrice();
        var collected = new LinkedList<ValidationResult>();
        for (var validator : filteredValidators) {
            if (validator.isUpdateOnly()) {
                Preconditions.checkState(context.getOldBet().isPresent());
            }
            var validationResult = handleDowngrade(newPrice, context.getOldBet(), validator)
                    .orElse(requireNonNull(validator.validate(context)));
            recorder.updateMetrics(validationResult, context.getSide(), validator.getName());
            collected.add(validationResult);
        }
        return requireNonNull(reduce(collected));
    }

    private Predicate<Validator> createPredicate(BetContext context) {
        var predicates = new LinkedList<Predicate<Validator>>();
        if (!context.getOldBet().isPresent()) {
            Predicate<Validator> updateOnly = Validator::isUpdateOnly;
            predicates.add(updateOnly.negate());
        }
        Predicate<Validator> priceRequired = Validator::isPriceRequired;
        if (context.getNewPrice().isPresent()) {
            predicates.add(priceRequired);
        } else {
            predicates.add(priceRequired.negate());
        }
        return predicates.stream().reduce(Predicate::and).get();
    }

}
