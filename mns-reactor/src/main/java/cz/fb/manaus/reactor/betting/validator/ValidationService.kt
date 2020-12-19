package cz.fb.manaus.reactor.betting.validator

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.model.Bet
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.price.Pricing
import org.springframework.stereotype.Service

@Service
class ValidationService(
        private val recorder: ValidationMetricsCollector
) {

    internal fun isDowngrade(newOne: Price?, oldOne: Bet?): Boolean {
        if (oldOne != null && newOne != null) {
            val oldPrice = oldOne.requestedPrice
            check(newOne.side == oldPrice.side)
            if (Pricing.isDowngrade(newOne.price, oldPrice.price, newOne.side)) {
                return true
            }
        }
        return false
    }

    internal fun reduce(results: List<ValidationResult>): ValidationResult {
        check(results.isNotEmpty())
        return results.find { it == ValidationResult.DROP }
                ?: results.find { it == ValidationResult.NOP }
                ?: ValidationResult.OK
    }

    fun validator(validators: List<Validator>): (BetEvent) -> ValidationResult = { event ->
        val filteredValidators = validators.filter(createPredicate(event))
        check(filteredValidators.isNotEmpty())

        if (filteredValidators.any { it.isUpdateOnly }) {
            check(event.oldBet != null)
        }

        val collected = filteredValidators
                .map { makeName(it) to validate(event, it) }
                .onEach { recorder.updateMetrics(it.second, event.side, it.first) }
                .map { it.second }
        reduce(collected)
    }

    private fun validate(event: BetEvent, validator: Validator): ValidationResult {
        val skip = validator.isDowngradeAccepting && isDowngrade(event.proposedPrice, event.oldBet?.remote)
        return if (skip) ValidationResult.OK else validator.validate(event)
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
