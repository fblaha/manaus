package cz.fb.manaus.reactor.betting.validator

import com.google.common.base.Preconditions
import com.google.common.base.Preconditions.checkState
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.Objects.requireNonNull

@Service
class ValidationService {

    @Autowired
    private lateinit var priceService: PriceService
    @Autowired
    private lateinit var recorder: ValidationMetricsCollector

    internal fun handleDowngrade(newOne: Optional<Price>, oldOne: Optional<Bet>, validator: Validator): Optional<ValidationResult> {
        if (oldOne.isPresent && newOne.isPresent) {
            val oldPrice = oldOne.get().requestedPrice
            checkState(newOne.get().side === requireNonNull(oldPrice.side), validator.javaClass)
            if (priceService.isDowngrade(newOne.get().price, oldPrice.price,
                            newOne.get().side) && validator.isDowngradeAccepting) {
                return Optional.of(ValidationResult.ACCEPT)
            }
        }
        return Optional.empty()
    }

    internal fun reduce(results: List<ValidationResult>): ValidationResult {
        checkState(!results.isEmpty())
        return ValidationResult.of(results.all { it.isSuccess })
    }

    fun validate(context: BetContext, validators: List<Validator>): ValidationResult {
        val filteredValidators = validators
                .filter(createPredicate(context))
        Preconditions.checkState(!filteredValidators.isEmpty())

        val newPrice = context.newPrice
        val collected = LinkedList<ValidationResult>()
        for (validator in filteredValidators) {
            if (validator.isUpdateOnly) {
                Preconditions.checkState(context.oldBet.isPresent)
            }
            val validationResult = handleDowngrade(newPrice, context.oldBet, validator)
                    .orElse(requireNonNull(validator.validate(context)))
            recorder.updateMetrics(validationResult, context.side, validator.name)
            collected.add(validationResult)
        }
        return requireNonNull(reduce(collected))
    }

    private fun createPredicate(context: BetContext): (Validator) -> Boolean {
        val predicates = LinkedList<(Validator) -> Boolean>()
        if (!context.oldBet.isPresent) {
            val updateOnly: (Validator) -> Boolean = { it.isUpdateOnly }
            predicates.add { !updateOnly(it) }
        }
        val priceRequired: (Validator) -> Boolean = { it.isPriceRequired }
        if (context.newPrice.isPresent) {
            predicates.add(priceRequired)
        } else {
            predicates.add { !priceRequired(it) }
        }
        return { validator -> predicates.all { it(validator) } }
    }

}
