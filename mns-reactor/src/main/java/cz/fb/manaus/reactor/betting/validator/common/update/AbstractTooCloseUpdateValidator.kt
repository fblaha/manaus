package cz.fb.manaus.reactor.betting.validator.common.update

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractTooCloseUpdateValidator(private val closeSteps: Set<Int>) : Validator {
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var priceService: PriceService

    override val isUpdateOnly: Boolean = true

    override fun validate(context: BetContext): ValidationResult {
        val oldOne = context.oldBet!!.requestedPrice.price
        val newOne = context.newPrice!!.price
        if (Price.priceEq(newOne, oldOne)) return ValidationResult.REJECT
        for (step in closeSteps) {
            Preconditions.checkArgument(step != 0)
            val closePrice: Double? = when {
                step > 0 -> roundingService.increment(oldOne, step)
                else -> roundingService.decrement(oldOne, -step)
            }
            if (closePrice != null && Price.priceEq(newOne, closePrice))
                return ValidationResult.REJECT
        }
        return ValidationResult.ACCEPT
    }

}
