package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.ischia.BackLoserBet
import cz.fb.manaus.ischia.LayLoserBet
import cz.fb.manaus.reactor.betting.BetContext
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.reactor.betting.validator.common.update.AbstractTooCloseUpdateValidator
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@BackLoserBet
@LayLoserBet
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("betfair")
class TooCloseUpdateValidator : AbstractTooCloseUpdateValidator(setOf(-1, 1)) {

    override fun validate(context: BetContext): ValidationResult {
        return if (context.isCounterHalfMatched) {
            ValidationResult.ACCEPT
        } else {
            super.validate(context)
        }
    }
}
