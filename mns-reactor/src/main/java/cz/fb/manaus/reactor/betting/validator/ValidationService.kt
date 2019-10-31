package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.price.PriceService
import org.springframework.stereotype.Service

@Service
class ValidationService(private val priceService: PriceService,
                        private val recorder: ValidationMetricsCollector) {

    internal fun handleDowngrade(newOne: Price?, oldOne: Bet?, isDowngradeAccepting: Boolean): ValidationResult? {
        if (oldOne != null && newOne != null) {
            val oldPrice = oldOne.requestedPrice
            check(newOne.side === oldPrice.side)
            val isDowngrade = priceService.isDowngrade(newOne.price, oldPrice.price, newOne.side)
            if (isDowngrade && isDowngradeAccepting) {
                return ValidationResult.OK
            }
        }
        return null
    }

    internal fun reduce(results: List<ValidationResult>): ValidationResult {
        check(results.isNotEmpty())
        return results.find { it === ValidationResult.DROP }
                ?: results.find { it === ValidationResult.NOP }
                ?: ValidationResult.OK
    }

    fun validate(event: BetEvent, validators: List<Validator>): ValidationResult {
        val filteredValidators = validators.filter(createPredicate(event))
        check(filteredValidators.isNotEmpty())

        if (filteredValidators.any { it.isUpdateOnly }) {
            check(event.oldBet != null)
        }

        val collected = filteredValidators
                .map { it.name to validate(event, it) }
                .onEach { recorder.updateMetrics(it.second, event.side, it.first) }
                .map { it.second }
        return reduce(collected)
    }

    private fun validate(event: BetEvent, validator: Validator): ValidationResult {
        val downgradeResult = handleDowngrade(event.newPrice, event.oldBet, validator.isDowngradeAccepting)
        return downgradeResult ?: validator.validate(event)
    }

    private fun createPredicate(event: BetEvent): (Validator) -> Boolean {
        return fun(validator: Validator): Boolean {
            if (validator.isUpdateOnly && event.oldBet == null) {
                return false
            }
            val provider = event.account.provider
            return provider.matches(validator)
        }
    }
}
