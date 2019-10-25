package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.model.priceEq
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractTooCloseUpdateValidator(private val closeSteps: Set<Int>) : Validator {
    @Autowired
    private lateinit var roundingService: RoundingService

    override val isUpdateOnly: Boolean = true

    override fun validate(context: BetContext): ValidationResult {
        val oldOne = context.oldBet!!.requestedPrice.price
        val newOne = context.newPrice!!.price
        if (newOne priceEq oldOne) return ValidationResult.REJECT
        val minPrice = context.account.provider.minPrice
        val tagPredicate = context.account.provider::matches
        val containsEqualPrice = closeSteps
                .onEach { require(it != 0) }
                .mapNotNull {
                    when {
                        it > 0 -> roundingService.increment(oldOne, it, tagPredicate)
                        else -> roundingService.decrement(oldOne, -it, minPrice, tagPredicate)
                    }
                }.any { newOne priceEq it }
        return ValidationResult.of(!containsEqualPrice)
    }

}
