package cz.fb.manaus.reactor.betting.validator.common.update

import com.google.common.base.Preconditions
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.price.PriceService
import cz.fb.manaus.reactor.rounding.RoundingService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

abstract class AbstractTooCloseUpdateValidator(private val closeSteps: Set<Int>) : Validator {
    @Autowired
    private lateinit var roundingService: RoundingService
    @Autowired
    private lateinit var priceService: PriceService

    override fun isUpdateOnly(): Boolean {
        return true
    }

    override fun validate(context: BetContext): ValidationResult {
        val oldOne = context.oldBet.get().requestedPrice.price
        val newOne = context.newPrice.get().price
        if (Price.priceEq(newOne, oldOne)) return ValidationResult.REJECT
        for (step in closeSteps) {
            Preconditions.checkArgument(step != 0)
            val closePrice: OptionalDouble = when {
                step > 0 -> roundingService.increment(oldOne, step)
                else -> roundingService.decrement(oldOne, -step)
            }
            if (closePrice.isPresent && Price.priceEq(newOne, closePrice.asDouble))
                return ValidationResult.REJECT
        }
        return ValidationResult.ACCEPT
    }

}
