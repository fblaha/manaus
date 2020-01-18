package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.ischia.BackUniverse
import cz.fb.manaus.ischia.LayUniverse
import cz.fb.manaus.reactor.betting.validator.Validator
import cz.fb.manaus.reactor.betting.validator.common.update.TooCloseUpdateEpsilonValidator
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@BackUniverse
@LayUniverse
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
object TheSamePriceUpdateEpsilonValidator : Validator by TooCloseUpdateEpsilonValidator(0.025) {

    override val isDowngradeAccepting: Boolean = false

}
