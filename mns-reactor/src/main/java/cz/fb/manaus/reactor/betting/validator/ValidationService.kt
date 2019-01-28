package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Service

@Service
class ValidationService(private val priceService: PriceService,
                        private val recorder: ValidationMetricsCollector) {

    internal fun handleDowngrade(newOne: Price?, oldOne: Bet?, isDowngradeAccepting: Boolean): ValidationResult? {
        if (oldOne != null && newOne != null) {
            val oldPrice = oldOne.requestedPrice
            check(newOne.side === oldPrice.side)
            if (priceService.isDowngrade(newOne.price, oldPrice.price,
                            newOne.side) && isDowngradeAccepting) {
                return ValidationResult.ACCEPT
            }
        }
        return null
    }

    internal fun reduce(results: List<ValidationResult>): ValidationResult {
        check(results.isNotEmpty())
        return ValidationResult.of(results.all { it.isSuccess })
    }

    fun validate(context: BetContext, validators: List<Validator>): ValidationResult {
        val filteredValidators = validators.filter(createPredicate(context))
        check(filteredValidators.isNotEmpty())

        if (filteredValidators.any { it.isUpdateOnly }) {
            check(context.oldBet != null)
        }

        val collected = filteredValidators
                .map { it.name to validate(context, it) }
                .onEach { recorder.updateMetrics(it.second, context.side, it.first) }
                .map { it.second }
        return reduce(collected)
    }

    private fun validate(context: BetContext, validator: Validator): ValidationResult {
        val downgradeResult = handleDowngrade(context.newPrice, context.oldBet, validator.isDowngradeAccepting)
        return downgradeResult ?: validator.validate(context)
    }

    private fun createPredicate(context: BetContext): (Validator) -> Boolean {
        val predicates = mutableListOf<(Validator) -> Boolean>()
        if (context.oldBet == null) {
            predicates.add { !it.isUpdateOnly }
        }
        if (context.newPrice != null) {
            predicates.add { it.isPriceRequired }
        } else {
            predicates.add { !it.isPriceRequired }
        }
        return { validator -> predicates.all { it(validator) } }
    }
}
