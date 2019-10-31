package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.reactor.betting.BetEvent
import cz.fb.manaus.reactor.betting.validator.UpdateOnlyValidator
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractTooCloseUpdateValidator(private val closeSteps: Set<Int>) : UpdateOnlyValidator {
    @Autowired
    private lateinit var roundingService: RoundingService

    override fun validate(event: BetEvent): ValidationResult {
        val oldOne = event.oldBet!!.requestedPrice.price
        val newOne = event.newPrice!!.price
        if (newOne priceEq oldOne) return ValidationResult.NOP
        val minPrice = event.account.provider.minPrice
        val tagPredicate = event.account.provider::matches
        val containsEqualPrice = closeSteps
                .onEach { require(it != 0) }
                .mapNotNull {
                    when {
                        it > 0 -> roundingService.increment(oldOne, it, tagPredicate)
                        else -> roundingService.decrement(oldOne, -it, minPrice, tagPredicate)
                    }
                }.any { newOne priceEq it }
        return if (containsEqualPrice) ValidationResult.NOP else ValidationResult.ACCEPT
    }

}
